#!/usr/bin/env python3
"""Benchmark Elasticsearch against Postgres FTS for product search workloads."""

from __future__ import annotations

import argparse
import asyncio
import json
import random
import time
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Dict, List, Optional

import aiohttp
import asyncpg
import yaml

from common import (
    DEFAULT_ELASTICSEARCH_INDEX,
    DEFAULT_ELASTICSEARCH_URL,
    DEFAULT_POSTGRES_DSN,
    ensure_parent_dir,
    env_or_default,
    now_utc_iso,
    summarize_latencies,
)


WORKLOAD_KEYWORD = "keyword_filters"
WORKLOAD_FACETS = "facets"
WORKLOAD_DEEP = "deep_pagination"
WORKLOADS = [WORKLOAD_KEYWORD, WORKLOAD_FACETS, WORKLOAD_DEEP]


class TransientBenchmarkError(RuntimeError):
    """Signals that the request should be retried."""


@dataclass(frozen=True)
class QueryParams:
    term: str
    min_price: float
    max_price: float
    category: Optional[str]
    brand: Optional[str]
    in_stock: bool = True
    deep_template_id: Optional[str] = None


class WorkloadModel:
    def __init__(self, cfg: Dict[str, Any]) -> None:
        self.cfg = cfg
        self.mix = cfg["mix"]
        self.limits = cfg["limits"]
        self.terms = cfg["terms"]
        self.categories = cfg["categories"]
        self.brands = cfg["brands"]
        self.price_ranges = [tuple(r) for r in cfg["price_ranges"]]
        self.deep_templates = self._build_deep_templates()

    def _build_deep_templates(self) -> List[QueryParams]:
        templates: List[QueryParams] = []
        template_count = int(self.limits.get("deep_template_count", 4))
        for i, term in enumerate(self.terms["common"][:template_count]):
            templates.append(
                QueryParams(
                    term=term,
                    min_price=0.0,
                    max_price=1200.0,
                    category=None,
                    brand=None,
                    deep_template_id=f"deep_{i}",
                )
            )
        return templates

    def choose_workload(self, rng: random.Random) -> str:
        roll = rng.random()
        if roll < self.mix[WORKLOAD_KEYWORD]:
            return WORKLOAD_KEYWORD
        if roll < self.mix[WORKLOAD_KEYWORD] + self.mix[WORKLOAD_FACETS]:
            return WORKLOAD_FACETS
        return WORKLOAD_DEEP

    def _choose_term(self, rng: random.Random, prefer_common: bool = False) -> str:
        if prefer_common:
            return rng.choice(self.terms["common"])

        bucket_roll = rng.random()
        if bucket_roll < 0.60:
            return rng.choice(self.terms["common"])
        if bucket_roll < 0.85:
            return rng.choice(self.terms["medium"])
        return rng.choice(self.terms["rare"])

    def _choose_price_range(self, rng: random.Random) -> tuple[float, float]:
        low, high = rng.choice(self.price_ranges)
        return float(low), float(high)

    def build_params(self, workload: str, rng: random.Random) -> QueryParams:
        if workload == WORKLOAD_DEEP:
            return rng.choice(self.deep_templates)

        min_price, max_price = self._choose_price_range(rng)
        category = rng.choice(self.categories) if rng.random() < 0.85 else None
        brand = rng.choice(self.brands) if rng.random() < 0.35 else None
        return QueryParams(
            term=self._choose_term(rng),
            min_price=min_price,
            max_price=max_price,
            category=category,
            brand=brand,
        )

    def fixed_correctness_queries(self, n: int = 20) -> List[QueryParams]:
        rng = random.Random(2026)
        queries: List[QueryParams] = []
        for _ in range(n):
            category = rng.choice(self.categories) if rng.random() < 0.75 else None
            min_price, max_price = (0.0, 600.0)
            queries.append(
                QueryParams(
                    term=rng.choice(self.terms["common"]),
                    min_price=min_price,
                    max_price=max_price,
                    category=category,
                    brand=None,
                )
            )
        return queries


class PostgresRunner:
    def __init__(self, dsn: str, timeout_sec: float) -> None:
        self.dsn = dsn
        self.timeout_sec = timeout_sec
        self.pool: Optional[asyncpg.Pool] = None

    async def connect(self, concurrency: int) -> None:
        self.pool = await asyncpg.create_pool(
            dsn=self.dsn,
            min_size=max(2, min(10, concurrency // 2)),
            max_size=max(4, concurrency + 4),
            command_timeout=self.timeout_sec,
        )

    async def close(self) -> None:
        if self.pool is not None:
            await self.pool.close()
            self.pool = None

    async def keyword_query(
        self,
        params: QueryParams,
        size: int,
        use_relevance_sort: bool = True,
    ) -> List[str]:
        order_clause = (
            "ts_rank(p.search_vector, q.query) DESC, p.popularity_score DESC"
            if use_relevance_sort
            else "p.popularity_score DESC, p.id ASC"
        )
        sql = f"""
            WITH q AS (SELECT plainto_tsquery('english', $1) AS query)
            SELECT p.id
            FROM products p, q
            WHERE p.search_vector @@ q.query
              AND ($2::text IS NULL OR p.category = $2)
              AND ($3::text IS NULL OR p.brand = $3)
              AND p.price BETWEEN $4 AND $5
              AND p.in_stock = $6
            ORDER BY {order_clause}
            LIMIT $7
        """
        assert self.pool is not None
        try:
            async with self.pool.acquire() as conn:
                rows = await conn.fetch(
                    sql,
                    params.term,
                    params.category,
                    params.brand,
                    params.min_price,
                    params.max_price,
                    params.in_stock,
                    size,
                    timeout=self.timeout_sec,
                )
        except (asyncio.TimeoutError, asyncpg.PostgresConnectionError) as exc:
            raise TransientBenchmarkError(str(exc)) from exc
        return [row["id"] for row in rows]

    async def facet_query(self, params: QueryParams, facet_size: int) -> Dict[str, Dict[str, int]]:
        facet_category_sql = """
            WITH q AS (SELECT plainto_tsquery('english', $1) AS query)
            SELECT p.category, COUNT(*) AS c
            FROM products p, q
            WHERE p.search_vector @@ q.query
              AND ($2::text IS NULL OR p.category = $2)
              AND ($3::text IS NULL OR p.brand = $3)
              AND p.price BETWEEN $4 AND $5
              AND p.in_stock = $6
            GROUP BY p.category
            ORDER BY c DESC
            LIMIT $7
        """

        facet_brand_sql = """
            WITH q AS (SELECT plainto_tsquery('english', $1) AS query)
            SELECT p.brand, COUNT(*) AS c
            FROM products p, q
            WHERE p.search_vector @@ q.query
              AND ($2::text IS NULL OR p.category = $2)
              AND ($3::text IS NULL OR p.brand = $3)
              AND p.price BETWEEN $4 AND $5
              AND p.in_stock = $6
            GROUP BY p.brand
            ORDER BY c DESC
            LIMIT $7
        """

        assert self.pool is not None
        try:
            async with self.pool.acquire() as conn:
                cat_rows = await conn.fetch(
                    facet_category_sql,
                    params.term,
                    params.category,
                    params.brand,
                    params.min_price,
                    params.max_price,
                    params.in_stock,
                    facet_size,
                    timeout=self.timeout_sec,
                )
                brand_rows = await conn.fetch(
                    facet_brand_sql,
                    params.term,
                    params.category,
                    params.brand,
                    params.min_price,
                    params.max_price,
                    params.in_stock,
                    facet_size,
                    timeout=self.timeout_sec,
                )
        except (asyncio.TimeoutError, asyncpg.PostgresConnectionError) as exc:
            raise TransientBenchmarkError(str(exc)) from exc

        return {
            "category": {row["category"]: int(row["c"]) for row in cat_rows},
            "brand": {row["brand"]: int(row["c"]) for row in brand_rows},
        }

    async def deep_pagination_query(
        self,
        params: QueryParams,
        deep_offset: int,
        size: int,
    ) -> List[str]:
        sql = """
            WITH q AS (SELECT plainto_tsquery('english', $1) AS query)
            SELECT p.id
            FROM products p, q
            WHERE p.search_vector @@ q.query
              AND p.price BETWEEN $2 AND $3
              AND p.in_stock = TRUE
            ORDER BY p.created_at DESC, p.id ASC
            OFFSET $4
            LIMIT $5
        """
        assert self.pool is not None
        try:
            async with self.pool.acquire() as conn:
                rows = await conn.fetch(
                    sql,
                    params.term,
                    params.min_price,
                    params.max_price,
                    deep_offset,
                    size,
                    timeout=self.timeout_sec,
                )
        except (asyncio.TimeoutError, asyncpg.PostgresConnectionError) as exc:
            raise TransientBenchmarkError(str(exc)) from exc

        return [row["id"] for row in rows]

    async def execute(self, workload: str, params: QueryParams, model: WorkloadModel) -> Dict[str, Any]:
        if workload == WORKLOAD_KEYWORD:
            ids = await self.keyword_query(params, int(model.limits["result_size"]))
            return {"ids": ids}
        if workload == WORKLOAD_FACETS:
            facets = await self.facet_query(params, int(model.limits["facet_size"]))
            return {"facets": facets}
        if workload == WORKLOAD_DEEP:
            ids = await self.deep_pagination_query(
                params,
                int(model.limits["deep_offset"]),
                int(model.limits["deep_size"]),
            )
            return {"ids": ids}
        raise ValueError(f"unknown workload: {workload}")


class ElasticsearchRunner:
    def __init__(self, base_url: str, index: str, timeout_sec: float) -> None:
        self.base_url = base_url.rstrip("/")
        self.index = index
        self.timeout = aiohttp.ClientTimeout(total=timeout_sec)
        self.session: Optional[aiohttp.ClientSession] = None
        self.pit_id: Optional[str] = None
        self.deep_cursor_by_template: Dict[str, List[Any]] = {}

    async def connect(self) -> None:
        self.session = aiohttp.ClientSession(timeout=self.timeout)

    async def close(self) -> None:
        if self.session is not None:
            if self.pit_id:
                await self.close_pit()
            await self.session.close()
            self.session = None

    async def _request(
        self,
        method: str,
        path: str,
        payload: Optional[Dict[str, Any]] = None,
        timeout_sec: Optional[float] = None,
    ) -> Dict[str, Any]:
        assert self.session is not None
        url = f"{self.base_url}{path}"
        try:
            request_kwargs: Dict[str, Any] = {"json": payload}
            if timeout_sec is not None:
                request_kwargs["timeout"] = aiohttp.ClientTimeout(total=timeout_sec)

            async with self.session.request(method, url, **request_kwargs) as resp:
                if resp.status in (429, 500, 502, 503, 504):
                    text = await resp.text()
                    raise TransientBenchmarkError(
                        f"elasticsearch transient status={resp.status}: {text[:200]}"
                    )
                if resp.status >= 400:
                    text = await resp.text()
                    raise RuntimeError(f"elasticsearch status={resp.status}: {text[:300]}")
                if resp.content_type == "application/json":
                    return await resp.json()
                text = await resp.text()
                return {"text": text}
        except (aiohttp.ClientError, asyncio.TimeoutError) as exc:
            raise TransientBenchmarkError(str(exc)) from exc

    def _query_filter_clauses(self, params: QueryParams) -> List[Dict[str, Any]]:
        clauses: List[Dict[str, Any]] = [
            {"range": {"price": {"gte": params.min_price, "lte": params.max_price}}},
            {"term": {"in_stock": True}},
        ]
        if params.category:
            clauses.append({"term": {"category": params.category}})
        if params.brand:
            clauses.append({"term": {"brand": params.brand}})
        return clauses

    def _keyword_payload(
        self,
        params: QueryParams,
        size: int,
        use_relevance_sort: bool = True,
    ) -> Dict[str, Any]:
        sort = (
            [{"_score": "desc"}, {"popularity_score": "desc"}]
            if use_relevance_sort
            else [{"popularity_score": "desc"}, {"id": "asc"}]
        )
        return {
            "size": size,
            "track_total_hits": False,
            "_source": False,
            "query": {
                "bool": {
                    "must": [
                        {
                            "multi_match": {
                                "query": params.term,
                                "fields": ["title^2", "description"],
                            }
                        }
                    ],
                    "filter": self._query_filter_clauses(params),
                }
            },
            "sort": sort,
        }

    async def keyword_query(
        self,
        params: QueryParams,
        size: int,
        use_relevance_sort: bool = True,
    ) -> List[str]:
        payload = self._keyword_payload(params, size, use_relevance_sort=use_relevance_sort)
        response = await self._request("POST", f"/{self.index}/_search", payload)
        hits = response.get("hits", {}).get("hits", [])
        return [hit.get("_id") for hit in hits if "_id" in hit]

    async def facet_query(self, params: QueryParams, size: int, facet_size: int) -> Dict[str, Dict[str, int]]:
        payload = self._keyword_payload(params, size, use_relevance_sort=True)
        payload["aggs"] = {
            "by_category": {"terms": {"field": "category", "size": facet_size}},
            "by_brand": {"terms": {"field": "brand", "size": facet_size}},
        }
        response = await self._request("POST", f"/{self.index}/_search", payload)
        aggs = response.get("aggregations", {})

        cat_buckets = aggs.get("by_category", {}).get("buckets", [])
        brand_buckets = aggs.get("by_brand", {}).get("buckets", [])
        return {
            "category": {bucket["key"]: int(bucket["doc_count"]) for bucket in cat_buckets},
            "brand": {bucket["key"]: int(bucket["doc_count"]) for bucket in brand_buckets},
        }

    async def open_pit(self, keep_alive: str = "10m") -> None:
        response = await self._request("POST", f"/{self.index}/_pit?keep_alive={keep_alive}")
        pit_id = response.get("id")
        if not pit_id:
            raise RuntimeError("failed to open PIT")
        self.pit_id = pit_id

    async def close_pit(self) -> None:
        if not self.pit_id:
            return
        pit_id = self.pit_id
        self.pit_id = None
        await self._request("DELETE", "/_pit", {"id": pit_id})

    def _deep_payload(
        self,
        params: QueryParams,
        size: int,
        search_after: Optional[List[Any]],
    ) -> Dict[str, Any]:
        if not self.pit_id:
            raise RuntimeError("PIT is not open")

        payload: Dict[str, Any] = {
            "size": size,
            "track_total_hits": False,
            "_source": False,
            "pit": {"id": self.pit_id, "keep_alive": "10m"},
            "query": {
                "bool": {
                    "must": [
                        {
                            "multi_match": {
                                "query": params.term,
                                "fields": ["title^2", "description"],
                            }
                        }
                    ],
                    "filter": [
                        {"range": {"price": {"gte": params.min_price, "lte": params.max_price}}},
                        {"term": {"in_stock": True}},
                    ],
                }
            },
            "sort": [{"created_at": "desc"}, {"id": "asc"}],
        }

        if search_after is not None:
            payload["search_after"] = search_after
        return payload

    async def precompute_deep_cursors(self, model: WorkloadModel) -> None:
        if not self.pit_id:
            await self.open_pit()

        deep_offset = int(model.limits["deep_offset"])
        page_size = int(model.limits["deep_precompute_batch"])
        precompute_timeout_sec = max(
            10.0,
            float(self.timeout.total) if self.timeout.total is not None else 10.0,
        )

        self.deep_cursor_by_template = {}
        for template in model.deep_templates:
            remaining = deep_offset
            search_after = None
            current_page_size = min(page_size, remaining)
            while remaining > 0:
                payload = self._deep_payload(template, current_page_size, search_after)
                try:
                    response = await self._request(
                        "POST",
                        "/_search",
                        payload,
                        timeout_sec=precompute_timeout_sec,
                    )
                except TransientBenchmarkError:
                    if current_page_size <= 50:
                        search_after = None
                        break
                    current_page_size = max(50, current_page_size // 2)
                    continue

                hits = response.get("hits", {}).get("hits", [])
                if len(hits) < current_page_size:
                    search_after = None
                    break
                search_after = hits[-1].get("sort")
                remaining -= current_page_size
                current_page_size = min(page_size, remaining)

            if search_after is not None and template.deep_template_id is not None:
                self.deep_cursor_by_template[template.deep_template_id] = search_after

        if not self.deep_cursor_by_template:
            raise RuntimeError("could not precompute any deep pagination cursors")

    async def deep_pagination_query(self, params: QueryParams, size: int) -> List[str]:
        template_id = params.deep_template_id
        if template_id is None:
            raise RuntimeError("missing deep_template_id")

        search_after = self.deep_cursor_by_template.get(template_id)
        if search_after is None:
            # Fallback to any available deep cursor.
            search_after = next(iter(self.deep_cursor_by_template.values()))

        payload = self._deep_payload(params, size, search_after)
        response = await self._request("POST", "/_search", payload)
        hits = response.get("hits", {}).get("hits", [])
        return [hit.get("_id") for hit in hits if "_id" in hit]

    async def execute(self, workload: str, params: QueryParams, model: WorkloadModel) -> Dict[str, Any]:
        if workload == WORKLOAD_KEYWORD:
            ids = await self.keyword_query(params, int(model.limits["result_size"]))
            return {"ids": ids}
        if workload == WORKLOAD_FACETS:
            facets = await self.facet_query(
                params,
                int(model.limits["result_size"]),
                int(model.limits["facet_size"]),
            )
            return {"facets": facets}
        if workload == WORKLOAD_DEEP:
            ids = await self.deep_pagination_query(params, int(model.limits["deep_size"]))
            return {"ids": ids}
        raise ValueError(f"unknown workload: {workload}")


async def run_phase(
    *,
    engine: str,
    duration_sec: int,
    concurrency: int,
    model: WorkloadModel,
    execute_fn,
    seed: int,
    max_retries: int,
    record_events: bool,
) -> List[Dict[str, Any]]:
    events: List[Dict[str, Any]] = []
    loop = asyncio.get_running_loop()
    deadline = loop.time() + duration_sec

    async def worker(worker_index: int) -> None:
        worker_rng = random.Random(seed + worker_index * 31)
        while loop.time() < deadline:
            workload = model.choose_workload(worker_rng)
            params = model.build_params(workload, worker_rng)

            started = time.perf_counter()
            ok = False
            error = ""

            for attempt in range(max_retries + 1):
                try:
                    await execute_fn(workload, params, model)
                    ok = True
                    break
                except TransientBenchmarkError as exc:
                    if attempt >= max_retries:
                        error = str(exc)
                        break
                    await asyncio.sleep(0.02 * (attempt + 1))
                except Exception as exc:  # pylint: disable=broad-except
                    error = str(exc)
                    break

            elapsed_ms = (time.perf_counter() - started) * 1000.0
            if record_events:
                events.append(
                    {
                        "engine": engine,
                        "workload": workload,
                        "latency_ms": round(elapsed_ms, 3),
                        "ok": ok,
                        "error": error,
                    }
                )

    tasks = [asyncio.create_task(worker(i)) for i in range(concurrency)]
    await asyncio.gather(*tasks)
    return events


def summarize_events(
    *,
    engine: str,
    events: List[Dict[str, Any]],
    duration_sec: int,
    concurrency: int,
) -> List[Dict[str, Any]]:
    summaries: List[Dict[str, Any]] = []

    for workload in WORKLOADS:
        rows = [event for event in events if event["workload"] == workload]
        total = len(rows)
        errors = sum(1 for row in rows if not row["ok"])
        latencies = [row["latency_ms"] for row in rows if row["ok"]]
        latency_stats = summarize_latencies(latencies)
        qps = total / duration_sec if duration_sec > 0 else 0.0
        error_rate = (errors / total) if total else 0.0

        summaries.append(
            {
                "engine": engine,
                "workload": workload,
                "concurrency": concurrency,
                "requests_total": total,
                "requests_successful": total - errors,
                "qps": round(qps, 3),
                "error_rate": round(error_rate, 6),
                "timestamp": now_utc_iso(),
                **{k: round(v, 3) for k, v in latency_stats.items()},
            }
        )

    return summaries


async def run_correctness_checks(
    model: WorkloadModel,
    postgres: PostgresRunner,
    elasticsearch: ElasticsearchRunner,
) -> Dict[str, Any]:
    checks = model.fixed_correctness_queries(20)
    non_empty_failures = 0
    overlap_failures = 0
    facet_failures = 0
    overlaps: List[float] = []

    for query in checks:
        pg_ids = await postgres.keyword_query(query, 20, use_relevance_sort=False)
        es_ids = await elasticsearch.keyword_query(query, 20, use_relevance_sort=False)

        if not pg_ids or not es_ids:
            non_empty_failures += 1
            continue

        overlap = len(set(pg_ids) & set(es_ids)) / 20.0
        overlaps.append(overlap)
        if overlap < 0.50:
            overlap_failures += 1

    for query in checks[:5]:
        pg_facets = await postgres.facet_query(query, 5)
        es_facets = await elasticsearch.facet_query(query, 20, 5)
        if not pg_facets.get("category") or not pg_facets.get("brand"):
            facet_failures += 1
        if not es_facets.get("category") or not es_facets.get("brand"):
            facet_failures += 1

    avg_overlap = sum(overlaps) / len(overlaps) if overlaps else 0.0
    passed = non_empty_failures == 0 and avg_overlap >= 0.50 and facet_failures == 0

    return {
        "passed": passed,
        "queries_checked": len(checks),
        "average_top20_overlap": round(avg_overlap, 4),
        "non_empty_failures": non_empty_failures,
        "overlap_failures": overlap_failures,
        "facet_failures": facet_failures,
        "threshold_top20_overlap": 0.50,
    }


def load_workload_model(path: Path) -> WorkloadModel:
    with path.open("r", encoding="utf-8") as fh:
        cfg = yaml.safe_load(fh)
    return WorkloadModel(cfg)


async def run_benchmark(args: argparse.Namespace) -> int:
    model = load_workload_model(args.workload_config)
    output_path = args.output
    ensure_parent_dir(output_path)

    run_payload: Dict[str, Any] = {
        "metadata": {
            "dataset_size": args.dataset_size,
            "concurrency": args.concurrency,
            "warmup_sec": args.warmup_sec,
            "duration_sec": args.duration_sec,
            "engine": args.engine,
            "generated_at": now_utc_iso(),
        },
        "correctness": None,
        "summaries": [],
        "raw_requests": [],
    }

    engines = [args.engine] if args.engine != "both" else ["postgres", "elasticsearch"]

    pg_runner: Optional[PostgresRunner] = None
    es_runner: Optional[ElasticsearchRunner] = None

    try:
        if "postgres" in engines:
            pg_runner = PostgresRunner(args.postgres_dsn, args.request_timeout_ms / 1000.0)
            await pg_runner.connect(args.concurrency)

        if "elasticsearch" in engines:
            es_runner = ElasticsearchRunner(
                args.elasticsearch_url,
                args.elasticsearch_index,
                args.request_timeout_ms / 1000.0,
            )
            await es_runner.connect()
            await es_runner.precompute_deep_cursors(model)

        if args.engine == "both":
            assert pg_runner is not None
            assert es_runner is not None
            print("running correctness checks")
            run_payload["correctness"] = await run_correctness_checks(model, pg_runner, es_runner)

        for engine_name in engines:
            print(f"warming up {engine_name} for {args.warmup_sec}s")
            runner = pg_runner if engine_name == "postgres" else es_runner
            assert runner is not None
            await run_phase(
                engine=engine_name,
                duration_sec=args.warmup_sec,
                concurrency=args.concurrency,
                model=model,
                execute_fn=runner.execute,
                seed=args.seed,
                max_retries=args.max_retries,
                record_events=False,
            )

            print(f"running measured benchmark for {engine_name} ({args.duration_sec}s)")
            measured_events = await run_phase(
                engine=engine_name,
                duration_sec=args.duration_sec,
                concurrency=args.concurrency,
                model=model,
                execute_fn=runner.execute,
                seed=args.seed + 100,
                max_retries=args.max_retries,
                record_events=True,
            )

            run_payload["raw_requests"].extend(measured_events)
            run_payload["summaries"].extend(
                summarize_events(
                    engine=engine_name,
                    events=measured_events,
                    duration_sec=args.duration_sec,
                    concurrency=args.concurrency,
                )
            )

    finally:
        if pg_runner is not None:
            await pg_runner.close()
        if es_runner is not None:
            await es_runner.close()

    with output_path.open("w", encoding="utf-8") as fh:
        json.dump(run_payload, fh, indent=2)

    print(f"wrote results to {output_path}")

    max_error_rate = max((item["error_rate"] for item in run_payload["summaries"]), default=0.0)
    if max_error_rate > 0.01:
        print(f"error budget violated: max error rate {max_error_rate:.4f} > 0.01")
        return 2

    if run_payload.get("correctness") and not run_payload["correctness"]["passed"]:
        print("correctness checks failed")
        return 3

    return 0


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Benchmark Elasticsearch vs Postgres")
    parser.add_argument(
        "--engine",
        choices=["postgres", "elasticsearch", "both"],
        default="both",
        help="Engine(s) to benchmark",
    )
    parser.add_argument("--dataset-size", type=int, default=300000)
    parser.add_argument("--concurrency", type=int, default=20)
    parser.add_argument("--warmup-sec", type=int, default=30)
    parser.add_argument("--duration-sec", type=int, default=120)
    parser.add_argument("--seed", type=int, default=42)
    parser.add_argument("--max-retries", type=int, default=2)
    parser.add_argument("--request-timeout-ms", type=int, default=5000)
    parser.add_argument(
        "--workload-config",
        type=Path,
        default=Path("workloads/default.yaml"),
    )
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument(
        "--postgres-dsn",
        default=env_or_default("POSTGRES_DSN", DEFAULT_POSTGRES_DSN),
    )
    parser.add_argument(
        "--elasticsearch-url",
        default=env_or_default("ELASTICSEARCH_URL", DEFAULT_ELASTICSEARCH_URL),
    )
    parser.add_argument(
        "--elasticsearch-index",
        default=env_or_default("ELASTICSEARCH_INDEX", DEFAULT_ELASTICSEARCH_INDEX),
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    return asyncio.run(run_benchmark(args))


if __name__ == "__main__":
    raise SystemExit(main())
