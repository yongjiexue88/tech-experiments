# Elasticsearch vs Postgres FTS Benchmark Report

Generated: 2026-02-22T19:32:35+00:00

## Run Configuration

- Dataset size: 50000
- Concurrency: 8
- Warmup: 8s
- Measured duration: 20s
- Engine mode: both

## Correctness Checks

- Passed: True
- Queries checked: 20
- Average top-20 overlap: 1.0
- Non-empty failures: 0
- Overlap failures: 0
- Facet failures: 0

## Per-Engine Metrics

| Engine | Workload | Requests | QPS | P50 (ms) | P95 (ms) | P99 (ms) | Error Rate |
|---|---|---:|---:|---:|---:|---:|---:|
| postgres | keyword_filters | 186 | 9.30 | 153.90 | 1244.39 | 10445.45 | 0.0000 |
| postgres | facets | 74 | 3.70 | 348.67 | 2544.50 | 6722.38 | 0.0000 |
| postgres | deep_pagination | 43 | 2.15 | 560.59 | 11810.55 | 14871.53 | 0.0000 |
| elasticsearch | keyword_filters | 734 | 36.70 | 84.74 | 305.36 | 939.87 | 0.0000 |
| elasticsearch | facets | 291 | 14.55 | 106.43 | 340.48 | 512.95 | 0.0000 |
| elasticsearch | deep_pagination | 187 | 9.35 | 90.91 | 287.38 | 597.74 | 0.0000 |

## Relative Improvement (Elasticsearch vs Postgres)

| Workload | P95 Latency Improvement % | QPS Improvement % | Error Rate Delta % |
|---|---:|---:|---:|
| deep_pagination | 97.57 | 334.88 | 0.00 |
| facets | 86.62 | 293.24 | 0.00 |
| keyword_filters | 75.46 | 294.62 | 0.00 |

## Interpretation

- Elasticsearch usually wins on faceting and deep pagination due to dedicated index structures and search-after execution.
- Postgres FTS can remain competitive for simpler keyword+filter workloads on moderate datasets.
- If deep pagination and rich aggregations are frequent, Elasticsearch is typically the better read path.
