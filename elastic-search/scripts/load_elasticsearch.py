#!/usr/bin/env python3
"""Load generated catalog data into Elasticsearch."""

from __future__ import annotations

import argparse
import json
import time
from pathlib import Path

import requests

from common import DEFAULT_ELASTICSEARCH_INDEX, DEFAULT_ELASTICSEARCH_URL, env_or_default


def ensure_es_ready(base_url: str, timeout_sec: int = 90) -> None:
    started = time.time()
    while True:
        try:
            r = requests.get(f"{base_url}/_cluster/health", timeout=3)
            if r.ok:
                return
        except requests.RequestException:
            pass
        if time.time() - started > timeout_sec:
            raise RuntimeError("Elasticsearch did not become ready in time")
        time.sleep(1)


def create_index(base_url: str, index: str) -> None:
    requests.delete(f"{base_url}/{index}", timeout=10)
    payload = {
        "settings": {
            "number_of_shards": 1,
            "number_of_replicas": 0,
            "refresh_interval": "-1",
        },
        "mappings": {
            "properties": {
                "id": {"type": "keyword"},
                "title": {"type": "text", "analyzer": "english"},
                "description": {"type": "text", "analyzer": "english"},
                "brand": {"type": "keyword"},
                "category": {"type": "keyword"},
                "price": {"type": "float"},
                "rating": {"type": "float"},
                "in_stock": {"type": "boolean"},
                "created_at": {"type": "date"},
                "popularity_score": {"type": "float"},
            }
        },
    }
    r = requests.put(f"{base_url}/{index}", json=payload, timeout=20)
    r.raise_for_status()


def send_bulk(base_url: str, body_lines: list[str]) -> tuple[int, int]:
    body = "\n".join(body_lines) + "\n"
    r = requests.post(
        f"{base_url}/_bulk?refresh=false",
        data=body.encode("utf-8"),
        headers={"Content-Type": "application/x-ndjson"},
        timeout=30,
    )
    r.raise_for_status()
    payload = r.json()

    failed = 0
    for item in payload.get("items", []):
        status = item.get("index", {}).get("status", 0)
        if status >= 300:
            failed += 1

    return len(payload.get("items", [])), failed


def finalize_index(base_url: str, index: str) -> None:
    r = requests.put(
        f"{base_url}/{index}/_settings",
        json={"index": {"refresh_interval": "1s"}},
        timeout=20,
    )
    r.raise_for_status()
    r = requests.post(f"{base_url}/{index}/_refresh", timeout=20)
    r.raise_for_status()


def main() -> int:
    parser = argparse.ArgumentParser(description="Load product JSONL into Elasticsearch")
    parser.add_argument("--input", type=Path, required=True, help="Input JSONL file")
    parser.add_argument(
        "--url",
        default=env_or_default("ELASTICSEARCH_URL", DEFAULT_ELASTICSEARCH_URL),
        help="Elasticsearch URL",
    )
    parser.add_argument(
        "--index",
        default=env_or_default("ELASTICSEARCH_INDEX", DEFAULT_ELASTICSEARCH_INDEX),
        help="Index name",
    )
    parser.add_argument("--batch-size", type=int, default=1000, help="Bulk batch size")
    args = parser.parse_args()

    if not args.input.exists():
        raise SystemExit(f"input does not exist: {args.input}")

    base_url = args.url.rstrip("/")
    started = time.perf_counter()

    print("waiting for elasticsearch")
    ensure_es_ready(base_url)
    print(f"creating index {args.index}")
    create_index(base_url, args.index)

    loaded = 0
    failed = 0
    bulk_lines: list[str] = []

    print("loading documents")
    with args.input.open("r", encoding="utf-8") as fh:
        for line in fh:
            doc = json.loads(line)
            bulk_lines.append(json.dumps({"index": {"_index": args.index, "_id": doc["id"]}}))
            bulk_lines.append(json.dumps(doc, separators=(",", ":")))

            if len(bulk_lines) >= args.batch_size * 2:
                indexed, batch_failed = send_bulk(base_url, bulk_lines)
                loaded += indexed
                failed += batch_failed
                bulk_lines.clear()
                if loaded % 50000 == 0:
                    print(f"indexed {loaded} docs")

    if bulk_lines:
        indexed, batch_failed = send_bulk(base_url, bulk_lines)
        loaded += indexed
        failed += batch_failed
        bulk_lines.clear()

    finalize_index(base_url, args.index)

    elapsed = time.perf_counter() - started
    print(f"done: indexed {loaded} docs ({failed} failed) in {elapsed:.2f}s")
    if failed > 0:
        raise SystemExit("bulk load had failed records")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
