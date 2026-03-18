#!/usr/bin/env python3
"""Generate markdown report from benchmark JSON output."""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any, Dict, List, Optional

from common import ensure_parent_dir, now_utc_iso


def index_summaries(summaries: List[Dict[str, Any]]) -> Dict[tuple[str, str], Dict[str, Any]]:
    result: Dict[tuple[str, str], Dict[str, Any]] = {}
    for row in summaries:
        result[(row["engine"], row["workload"])] = row
    return result


def pct_change(new_value: float, old_value: float) -> Optional[float]:
    if old_value == 0:
        return None
    return (new_value - old_value) / old_value * 100.0


def fmt(value: Optional[float], digits: int = 2) -> str:
    if value is None:
        return "n/a"
    return f"{value:.{digits}f}"


def build_report(payload: Dict[str, Any]) -> str:
    summaries = payload.get("summaries", [])
    indexed = index_summaries(summaries)
    metadata = payload.get("metadata", {})
    correctness = payload.get("correctness")

    lines: List[str] = []
    lines.append("# Elasticsearch vs Postgres FTS Benchmark Report")
    lines.append("")
    lines.append(f"Generated: {now_utc_iso()}")
    lines.append("")
    lines.append("## Run Configuration")
    lines.append("")
    lines.append(f"- Dataset size: {metadata.get('dataset_size')}")
    lines.append(f"- Concurrency: {metadata.get('concurrency')}")
    lines.append(f"- Warmup: {metadata.get('warmup_sec')}s")
    lines.append(f"- Measured duration: {metadata.get('duration_sec')}s")
    lines.append(f"- Engine mode: {metadata.get('engine')}")
    lines.append("")

    if correctness is not None:
        lines.append("## Correctness Checks")
        lines.append("")
        lines.append(f"- Passed: {correctness.get('passed')}")
        lines.append(f"- Queries checked: {correctness.get('queries_checked')}")
        lines.append(f"- Average top-20 overlap: {correctness.get('average_top20_overlap')}")
        lines.append(f"- Non-empty failures: {correctness.get('non_empty_failures')}")
        lines.append(f"- Overlap failures: {correctness.get('overlap_failures')}")
        lines.append(f"- Facet failures: {correctness.get('facet_failures')}")
        lines.append("")

    lines.append("## Per-Engine Metrics")
    lines.append("")
    lines.append(
        "| Engine | Workload | Requests | QPS | P50 (ms) | P95 (ms) | P99 (ms) | Error Rate |"
    )
    lines.append("|---|---|---:|---:|---:|---:|---:|---:|")
    for row in summaries:
        lines.append(
            "| {engine} | {workload} | {requests_total} | {qps:.2f} | {p50:.2f} | {p95:.2f} | {p99:.2f} | {err:.4f} |".format(
                engine=row["engine"],
                workload=row["workload"],
                requests_total=row["requests_total"],
                qps=row["qps"],
                p50=row["latency_ms_p50"],
                p95=row["latency_ms_p95"],
                p99=row["latency_ms_p99"],
                err=row["error_rate"],
            )
        )

    workloads = sorted({row["workload"] for row in summaries})
    if ("postgres", workloads[0]) in indexed and ("elasticsearch", workloads[0]) in indexed:
        lines.append("")
        lines.append("## Relative Improvement (Elasticsearch vs Postgres)")
        lines.append("")
        lines.append(
            "| Workload | P95 Latency Improvement % | QPS Improvement % | Error Rate Delta % |"
        )
        lines.append("|---|---:|---:|---:|")

        for workload in workloads:
            pg = indexed.get(("postgres", workload))
            es = indexed.get(("elasticsearch", workload))
            if not pg or not es:
                continue

            if pg["latency_ms_p95"] == 0:
                p95_improvement = None
            else:
                p95_improvement = ((pg["latency_ms_p95"] - es["latency_ms_p95"]) / pg["latency_ms_p95"]) * 100.0
            qps_improvement = pct_change(es["qps"], pg["qps"])
            err_delta = (es["error_rate"] - pg["error_rate"]) * 100.0

            lines.append(
                "| {workload} | {p95} | {qps} | {err} |".format(
                    workload=workload,
                    p95=fmt(p95_improvement),
                    qps=fmt(qps_improvement),
                    err=fmt(err_delta),
                )
            )

        lines.append("")
        lines.append("## Interpretation")
        lines.append("")
        lines.append("- Elasticsearch usually wins on faceting and deep pagination due to dedicated index structures and search-after execution.")
        lines.append("- Postgres FTS can remain competitive for simpler keyword+filter workloads on moderate datasets.")
        lines.append("- If deep pagination and rich aggregations are frequent, Elasticsearch is typically the better read path.")

    return "\n".join(lines) + "\n"


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate markdown report from benchmark JSON")
    parser.add_argument("--input", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    if not args.input.exists():
        raise SystemExit(f"input does not exist: {args.input}")

    with args.input.open("r", encoding="utf-8") as fh:
        payload = json.load(fh)

    report = build_report(payload)
    ensure_parent_dir(args.output)
    with args.output.open("w", encoding="utf-8") as fh:
        fh.write(report)

    print(f"wrote report to {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
