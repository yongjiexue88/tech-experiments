#!/usr/bin/env python3
"""Generate synthetic product catalog JSONL with Zipf-like term distribution."""

from __future__ import annotations

import argparse
import json
import random
from datetime import datetime, timedelta, timezone
from pathlib import Path
from typing import List, Tuple

from common import ensure_parent_dir


CATEGORIES = [
    "electronics",
    "books",
    "home",
    "beauty",
    "sports",
    "grocery",
    "automotive",
    "toys",
    "office",
    "fashion",
]

BRANDS = [
    "apex",
    "nova",
    "zenith",
    "orbit",
    "lumen",
    "summit",
    "vector",
    "atlas",
    "quanta",
    "pixel",
    "forge",
    "ember",
    "tidal",
    "sterling",
    "cascade",
    "momentum",
    "horizon",
    "vantage",
    "sierra",
    "arc",
]

COMMON_TERMS = [
    "wireless",
    "smart",
    "premium",
    "portable",
    "durable",
    "compact",
    "eco",
    "gaming",
    "pro",
    "classic",
]

MEDIUM_TERMS = [
    "ergonomic",
    "modular",
    "precision",
    "artisan",
    "ultralight",
    "balanced",
    "hybrid",
    "studio",
    "tactical",
    "thermal",
]

RARE_TERMS = [
    "cryogenic",
    "antistatic",
    "photonic",
    "hyperlocal",
    "biodegradable",
    "retrofit",
    "noiseless",
    "aerospace",
]


def build_vocabulary() -> Tuple[List[str], List[float]]:
    tail_terms = [f"token_{i:03d}" for i in range(1, 201)]
    vocabulary = COMMON_TERMS + MEDIUM_TERMS + RARE_TERMS + tail_terms
    alpha = 1.07
    weights = [1.0 / ((i + 1) ** alpha) for i in range(len(vocabulary))]
    return vocabulary, weights


def random_timestamp(rng: random.Random) -> str:
    start = datetime(2020, 1, 1, tzinfo=timezone.utc)
    now = datetime.now(timezone.utc)
    total_seconds = int((now - start).total_seconds())
    offset_seconds = rng.randrange(total_seconds)
    ts = start + timedelta(seconds=offset_seconds)
    return ts.replace(microsecond=0).isoformat().replace("+00:00", "Z")


def clamp(value: float, low: float, high: float) -> float:
    return max(low, min(high, value))


def generate_record(index: int, rng: random.Random, vocabulary: List[str], weights: List[float]) -> dict:
    title_tokens = rng.choices(vocabulary, weights=weights, k=rng.randint(4, 8))
    desc_tokens = rng.choices(vocabulary, weights=weights, k=rng.randint(22, 46))

    title = " ".join(token.capitalize() for token in title_tokens)
    description = " ".join(desc_tokens)
    price = round(clamp(rng.lognormvariate(3.45, 0.65), 3.0, 3000.0), 2)
    rating = round(clamp(rng.normalvariate(4.1, 0.6), 1.0, 5.0), 1)
    in_stock = rng.random() < 0.82
    popularity_score = round(clamp(rng.paretovariate(1.3) * 8.0, 0.01, 10000.0), 4)

    return {
        "id": f"prod-{index:09d}",
        "title": title,
        "description": description,
        "brand": rng.choice(BRANDS),
        "category": rng.choice(CATEGORIES),
        "price": price,
        "rating": rating,
        "in_stock": in_stock,
        "created_at": random_timestamp(rng),
        "popularity_score": popularity_score,
    }


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate synthetic product catalog JSONL")
    parser.add_argument("--rows", type=int, default=300000, help="Number of products to generate")
    parser.add_argument("--seed", type=int, default=42, help="Random seed")
    parser.add_argument("--out", type=Path, required=True, help="Output JSONL path")
    args = parser.parse_args()

    if args.rows <= 0:
        raise SystemExit("--rows must be > 0")

    rng = random.Random(args.seed)
    vocabulary, weights = build_vocabulary()

    ensure_parent_dir(args.out)
    with args.out.open("w", encoding="utf-8") as fh:
        for i in range(1, args.rows + 1):
            record = generate_record(i, rng, vocabulary, weights)
            fh.write(json.dumps(record, separators=(",", ":")) + "\n")
            if i % 50000 == 0:
                print(f"generated {i} rows")

    print(f"done: wrote {args.rows} rows to {args.out}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
