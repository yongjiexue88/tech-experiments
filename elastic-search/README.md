# Elasticsearch vs Postgres FTS Benchmark

This project is a local, reproducible benchmark to demonstrate when Elasticsearch outperforms Postgres Full-Text Search for product-search workloads.

## What It Measures

- Keyword search + filters + relevance sort
- Keyword search + filters + facet counts
- Deep pagination (`OFFSET` in Postgres vs `search_after` + PIT in Elasticsearch)

Metrics generated per engine and per workload:

- `latency_ms_p50`
- `latency_ms_p95`
- `latency_ms_p99`
- `qps`
- `error_rate`

## Project Structure

- `docker-compose.yml` - local Postgres + Elasticsearch stack
- `scripts/generate_data.py` - seeded synthetic data generator (JSONL)
- `scripts/load_postgres.py` - schema + bulk load + indexes for Postgres FTS
- `scripts/load_elasticsearch.py` - index mapping + bulk load for Elasticsearch
- `scripts/benchmark.py` - async benchmark harness + correctness checks
- `scripts/report.py` - markdown report generation
- `scripts/visualize.py` - HTML chart dashboard generation
- `workloads/default.yaml` - query mix and parameter config

## Quick Start (Full Run)

```bash
cd /Users/yongjiexue/Documents/GitHub/tech-experiments/elastic-search
cp .env.example .env
docker compose up -d
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
python3 scripts/generate_data.py --rows 300000 --seed 42 --out data/products.jsonl
python3 scripts/load_postgres.py --input data/products.jsonl
python3 scripts/load_elasticsearch.py --input data/products.jsonl
python3 scripts/benchmark.py --engine both --dataset-size 300000 --concurrency 20 --duration-sec 120 --request-timeout-ms 5000 --output results/run.json
python3 scripts/report.py --input results/run.json --output results/report.md
python3 scripts/visualize.py --input results/run.json --output results/dashboard.html
```

## Smoke Test (Fast)

```bash
cd /Users/yongjiexue/Documents/GitHub/tech-experiments/elastic-search
cp .env.example .env
docker compose up -d
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
python3 scripts/generate_data.py --rows 50000 --seed 42 --out data/products-50k.jsonl
python3 scripts/load_postgres.py --input data/products-50k.jsonl
python3 scripts/load_elasticsearch.py --input data/products-50k.jsonl
python3 scripts/benchmark.py --engine both --dataset-size 50000 --concurrency 8 --warmup-sec 8 --duration-sec 20 --request-timeout-ms 5000 --output results/smoke-run.json
python3 scripts/report.py --input results/smoke-run.json --output results/smoke-report.md
python3 scripts/visualize.py --input results/smoke-run.json --output results/smoke-dashboard.html
```

## Environment Variables

Defaults are provided in `.env.example`.

- `POSTGRES_DSN` (default: `postgresql://postgres:postgres@localhost:5432/searchbench`)
- `ELASTICSEARCH_URL` (default: `http://localhost:9200`)
- `ELASTICSEARCH_INDEX` (default: `products`)

If local ports are occupied, update `.env`:

- `POSTGRES_PORT=55432` (or any free port)
- `ELASTICSEARCH_PORT=59200` (or any free port)
- `POSTGRES_DSN=postgresql://postgres:postgres@localhost:55432/searchbench`
- `ELASTICSEARCH_URL=http://localhost:59200`

## Notes

- Elasticsearch is configured as single-node with security disabled for local experiments only.
- Benchmark exits non-zero if error rate exceeds 1% or correctness checks fail.
- Workload and term distributions are configurable in `workloads/default.yaml`.
