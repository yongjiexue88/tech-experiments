#!/usr/bin/env python3
"""Shared helpers for the Elasticsearch vs Postgres benchmark."""

from __future__ import annotations

import json
import os
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional


DEFAULT_POSTGRES_DSN = "postgresql://postgres:postgres@localhost:5432/searchbench"
DEFAULT_ELASTICSEARCH_URL = "http://localhost:9200"
DEFAULT_ELASTICSEARCH_INDEX = "products"
_DOTENV_CACHE: Optional[Dict[str, str]] = None


def _load_dotenv() -> Dict[str, str]:
    global _DOTENV_CACHE
    if _DOTENV_CACHE is not None:
        return _DOTENV_CACHE

    candidates = [
        Path.cwd() / ".env",
        Path(__file__).resolve().parent.parent / ".env",
    ]

    env_map: Dict[str, str] = {}
    for path in candidates:
        if not path.exists():
            continue
        with path.open("r", encoding="utf-8") as fh:
            for raw_line in fh:
                line = raw_line.strip()
                if not line or line.startswith("#") or "=" not in line:
                    continue
                key, value = line.split("=", 1)
                env_map[key.strip()] = value.strip()
        break

    _DOTENV_CACHE = env_map
    return env_map


def env_or_default(name: str, default: str) -> str:
    value = os.getenv(name)
    if value is None or value.strip() == "":
        dotenv = _load_dotenv()
        return dotenv.get(name, default)
    return value


def load_jsonl(path: Path) -> Iterable[Dict[str, Any]]:
    with path.open("r", encoding="utf-8") as fh:
        for line in fh:
            line = line.strip()
            if not line:
                continue
            yield json.loads(line)


def ensure_parent_dir(path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)


def percentile(values: List[float], p: float) -> float:
    if not values:
        return 0.0
    sorted_values = sorted(values)
    if len(sorted_values) == 1:
        return float(sorted_values[0])
    rank = (len(sorted_values) - 1) * p
    low = int(rank)
    high = min(low + 1, len(sorted_values) - 1)
    weight = rank - low
    return float(sorted_values[low] * (1.0 - weight) + sorted_values[high] * weight)


def now_utc_iso() -> str:
    return datetime.now(timezone.utc).replace(microsecond=0).isoformat()


def summarize_latencies(values: List[float]) -> Dict[str, float]:
    return {
        "latency_ms_p50": percentile(values, 0.50),
        "latency_ms_p95": percentile(values, 0.95),
        "latency_ms_p99": percentile(values, 0.99),
    }
