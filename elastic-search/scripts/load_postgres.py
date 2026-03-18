#!/usr/bin/env python3
"""Load generated catalog data into Postgres with FTS indexes."""

from __future__ import annotations

import argparse
import csv
import io
import json
import time
from pathlib import Path

import psycopg2

from common import DEFAULT_POSTGRES_DSN, env_or_default


CREATE_TABLE_SQL = """
DROP TABLE IF EXISTS products;
CREATE TABLE products (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    brand TEXT NOT NULL,
    category TEXT NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    rating DOUBLE PRECISION NOT NULL,
    in_stock BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    popularity_score DOUBLE PRECISION NOT NULL,
    search_vector tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(description, '')), 'B')
    ) STORED
);
"""

CREATE_INDEX_SQL = """
CREATE INDEX idx_products_search_vector ON products USING GIN (search_vector);
CREATE INDEX idx_products_category ON products (category);
CREATE INDEX idx_products_brand ON products (brand);
CREATE INDEX idx_products_price ON products (price);
CREATE INDEX idx_products_created_at ON products (created_at DESC);
"""

COPY_SQL = """
COPY products (
    id, title, description, brand, category, price,
    rating, in_stock, created_at, popularity_score
)
FROM STDIN WITH (FORMAT CSV)
"""


def flush_batch(cur: psycopg2.extensions.cursor, batch: list[list[object]]) -> None:
    if not batch:
        return
    buf = io.StringIO()
    writer = csv.writer(buf)
    writer.writerows(batch)
    buf.seek(0)
    cur.copy_expert(COPY_SQL, buf)


def main() -> int:
    parser = argparse.ArgumentParser(description="Load product JSONL into Postgres")
    parser.add_argument("--input", type=Path, required=True, help="Input JSONL file")
    parser.add_argument(
        "--dsn",
        default=env_or_default("POSTGRES_DSN", DEFAULT_POSTGRES_DSN),
        help="Postgres DSN",
    )
    parser.add_argument("--batch-size", type=int, default=5000, help="Rows per COPY batch")
    args = parser.parse_args()

    if not args.input.exists():
        raise SystemExit(f"input does not exist: {args.input}")

    started = time.perf_counter()
    with psycopg2.connect(args.dsn) as conn:
        conn.autocommit = False
        with conn.cursor() as cur:
            print("creating table")
            cur.execute(CREATE_TABLE_SQL)
            conn.commit()

            print("loading rows")
            rows_loaded = 0
            batch: list[list[object]] = []
            with args.input.open("r", encoding="utf-8") as fh:
                for line in fh:
                    record = json.loads(line)
                    batch.append(
                        [
                            record["id"],
                            record["title"],
                            record["description"],
                            record["brand"],
                            record["category"],
                            record["price"],
                            record["rating"],
                            record["in_stock"],
                            record["created_at"],
                            record["popularity_score"],
                        ]
                    )
                    if len(batch) >= args.batch_size:
                        flush_batch(cur, batch)
                        rows_loaded += len(batch)
                        batch.clear()
                        if rows_loaded % 50000 == 0:
                            conn.commit()
                            print(f"loaded {rows_loaded} rows")

            if batch:
                flush_batch(cur, batch)
                rows_loaded += len(batch)
                batch.clear()

            conn.commit()
            print(f"loaded total rows: {rows_loaded}")

            print("creating indexes")
            cur.execute(CREATE_INDEX_SQL)
            cur.execute("ANALYZE products;")
            conn.commit()

            cur.execute("SELECT COUNT(*) FROM products;")
            final_count = cur.fetchone()[0]

    elapsed = time.perf_counter() - started
    print(f"done: postgres contains {final_count} rows in {elapsed:.2f}s")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
