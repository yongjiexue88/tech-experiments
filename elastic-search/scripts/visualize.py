#!/usr/bin/env python3
"""Generate an HTML visualization dashboard from benchmark JSON results."""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any, Dict, List, Optional

from common import ensure_parent_dir, now_utc_iso


WORKLOAD_LABELS = {
    "keyword_filters": "Keyword + Filters",
    "facets": "Facets",
    "deep_pagination": "Deep Pagination",
}


def format_pct(value: Optional[float]) -> str:
    if value is None:
        return "n/a"
    return f"{value:.2f}%"


def build_workload_rows(summaries: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    by_pair: Dict[tuple[str, str], Dict[str, Any]] = {}
    for row in summaries:
        by_pair[(row["engine"], row["workload"])] = row

    rows: List[Dict[str, Any]] = []
    for workload in ["keyword_filters", "facets", "deep_pagination"]:
        pg = by_pair.get(("postgres", workload))
        es = by_pair.get(("elasticsearch", workload))
        if not pg or not es:
            continue

        p95_improvement = None
        if pg["latency_ms_p95"] > 0:
            p95_improvement = (
                (pg["latency_ms_p95"] - es["latency_ms_p95"]) / pg["latency_ms_p95"]
            ) * 100.0

        qps_improvement = None
        if pg["qps"] > 0:
            qps_improvement = ((es["qps"] - pg["qps"]) / pg["qps"]) * 100.0

        rows.append(
            {
                "workload": workload,
                "label": WORKLOAD_LABELS.get(workload, workload),
                "pg_p95": pg["latency_ms_p95"],
                "es_p95": es["latency_ms_p95"],
                "pg_qps": pg["qps"],
                "es_qps": es["qps"],
                "p95_improvement": p95_improvement,
                "qps_improvement": qps_improvement,
                "pg_error": pg["error_rate"],
                "es_error": es["error_rate"],
            }
        )

    return rows


def render_dashboard(payload: Dict[str, Any]) -> str:
    metadata = payload.get("metadata", {})
    summaries = payload.get("summaries", [])
    correctness = payload.get("correctness")

    rows = build_workload_rows(summaries)

    labels = [row["label"] for row in rows]
    pg_p95 = [row["pg_p95"] for row in rows]
    es_p95 = [row["es_p95"] for row in rows]
    pg_qps = [row["pg_qps"] for row in rows]
    es_qps = [row["es_qps"] for row in rows]
    p95_gain = [row["p95_improvement"] or 0.0 for row in rows]
    qps_gain = [row["qps_improvement"] or 0.0 for row in rows]

    table_rows = "\n".join(
        f"""
        <tr>
          <td>{row['label']}</td>
          <td>{row['pg_p95']:.2f}</td>
          <td>{row['es_p95']:.2f}</td>
          <td>{format_pct(row['p95_improvement'])}</td>
          <td>{row['pg_qps']:.2f}</td>
          <td>{row['es_qps']:.2f}</td>
          <td>{format_pct(row['qps_improvement'])}</td>
        </tr>
        """.strip()
        for row in rows
    )

    correctness_html = ""
    if correctness is not None:
        correctness_html = f"""
        <div class=\"card\">
          <h3>Correctness</h3>
          <p><strong>Passed:</strong> {correctness.get('passed')}</p>
          <p><strong>Top-20 overlap:</strong> {correctness.get('average_top20_overlap')}</p>
          <p><strong>Queries checked:</strong> {correctness.get('queries_checked')}</p>
        </div>
        """

    return f"""<!doctype html>
<html lang=\"en\">
<head>
  <meta charset=\"utf-8\" />
  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
  <title>Search Benchmark Dashboard</title>
  <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>
  <style>
    :root {{
      --bg: #f8f5f0;
      --panel: #ffffff;
      --ink: #1a2a33;
      --muted: #5e6e78;
      --accent-a: #0f8b8d;
      --accent-b: #ee6c4d;
      --accent-c: #3d5a80;
      --line: #d7dde2;
    }}

    body {{
      margin: 0;
      font-family: "Avenir Next", "Segoe UI", sans-serif;
      color: var(--ink);
      background: radial-gradient(circle at top left, #fff7ec 0%, var(--bg) 55%);
    }}

    .page {{
      max-width: 1100px;
      margin: 0 auto;
      padding: 28px 18px 40px;
    }}

    h1 {{
      margin: 0 0 8px;
      font-size: 30px;
      letter-spacing: 0.2px;
    }}

    .sub {{
      margin: 0 0 18px;
      color: var(--muted);
    }}

    .grid {{
      display: grid;
      gap: 14px;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      margin-bottom: 16px;
    }}

    .card {{
      background: var(--panel);
      border: 1px solid var(--line);
      border-radius: 12px;
      padding: 14px;
      box-shadow: 0 4px 20px rgba(20, 20, 20, 0.04);
    }}

    .card h3 {{
      margin: 0 0 6px;
      font-size: 16px;
    }}

    .row {{
      display: grid;
      gap: 14px;
      grid-template-columns: 1fr;
      margin-bottom: 14px;
    }}

    @media (min-width: 980px) {{
      .row.two {{
        grid-template-columns: 1fr 1fr;
      }}
    }}

    table {{
      width: 100%;
      border-collapse: collapse;
      font-size: 14px;
      background: var(--panel);
      border-radius: 12px;
      overflow: hidden;
      border: 1px solid var(--line);
    }}

    th, td {{
      padding: 10px;
      border-bottom: 1px solid var(--line);
      text-align: left;
      white-space: nowrap;
    }}

    th {{
      background: #f0f4f7;
      font-weight: 600;
    }}

    tr:last-child td {{
      border-bottom: none;
    }}

    .footer {{
      margin-top: 18px;
      color: var(--muted);
      font-size: 12px;
    }}

    canvas {{
      max-height: 320px;
    }}
  </style>
</head>
<body>
  <div class=\"page\">
    <h1>Elasticsearch vs Postgres FTS</h1>
    <p class=\"sub\">Local benchmark dashboard</p>

    <div class=\"grid\">
      <div class=\"card\">
        <h3>Dataset</h3>
        <p>{metadata.get('dataset_size')}</p>
      </div>
      <div class=\"card\">
        <h3>Concurrency</h3>
        <p>{metadata.get('concurrency')}</p>
      </div>
      <div class=\"card\">
        <h3>Measured Duration</h3>
        <p>{metadata.get('duration_sec')}s</p>
      </div>
      {correctness_html}
    </div>

    <div class=\"row two\">
      <div class=\"card\">
        <h3>P95 Latency (ms)</h3>
        <canvas id=\"latencyChart\"></canvas>
      </div>
      <div class=\"card\">
        <h3>QPS</h3>
        <canvas id=\"qpsChart\"></canvas>
      </div>
    </div>

    <div class=\"row two\">
      <div class=\"card\">
        <h3>P95 Latency Improvement (%)</h3>
        <canvas id=\"p95GainChart\"></canvas>
      </div>
      <div class=\"card\">
        <h3>QPS Improvement (%)</h3>
        <canvas id=\"qpsGainChart\"></canvas>
      </div>
    </div>

    <div class=\"row\">
      <table>
        <thead>
          <tr>
            <th>Workload</th>
            <th>Postgres P95</th>
            <th>Elastic P95</th>
            <th>P95 Improve</th>
            <th>Postgres QPS</th>
            <th>Elastic QPS</th>
            <th>QPS Improve</th>
          </tr>
        </thead>
        <tbody>
          {table_rows}
        </tbody>
      </table>
    </div>

    <p class=\"footer\">Generated at {now_utc_iso()}</p>
  </div>

  <script>
    const labels = {json.dumps(labels)};
    const pgP95 = {json.dumps(pg_p95)};
    const esP95 = {json.dumps(es_p95)};
    const pgQps = {json.dumps(pg_qps)};
    const esQps = {json.dumps(es_qps)};
    const p95Gain = {json.dumps(p95_gain)};
    const qpsGain = {json.dumps(qps_gain)};

    const commonOpts = {{
      responsive: true,
      maintainAspectRatio: false,
      plugins: {{
        legend: {{ position: 'bottom' }}
      }}
    }};

    new Chart(document.getElementById('latencyChart'), {{
      type: 'bar',
      data: {{
        labels,
        datasets: [
          {{ label: 'Postgres', data: pgP95, backgroundColor: '#ee6c4d' }},
          {{ label: 'Elasticsearch', data: esP95, backgroundColor: '#0f8b8d' }}
        ]
      }},
      options: commonOpts
    }});

    new Chart(document.getElementById('qpsChart'), {{
      type: 'bar',
      data: {{
        labels,
        datasets: [
          {{ label: 'Postgres', data: pgQps, backgroundColor: '#ee6c4d' }},
          {{ label: 'Elasticsearch', data: esQps, backgroundColor: '#0f8b8d' }}
        ]
      }},
      options: commonOpts
    }});

    new Chart(document.getElementById('p95GainChart'), {{
      type: 'bar',
      data: {{
        labels,
        datasets: [
          {{ label: 'P95 improvement %', data: p95Gain, backgroundColor: '#3d5a80' }}
        ]
      }},
      options: commonOpts
    }});

    new Chart(document.getElementById('qpsGainChart'), {{
      type: 'bar',
      data: {{
        labels,
        datasets: [
          {{ label: 'QPS improvement %', data: qpsGain, backgroundColor: '#3d5a80' }}
        ]
      }},
      options: commonOpts
    }});
  </script>
</body>
</html>
"""


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate HTML benchmark visualization")
    parser.add_argument("--input", type=Path, required=True, help="Input run JSON")
    parser.add_argument("--output", type=Path, required=True, help="Output HTML path")
    args = parser.parse_args()

    if not args.input.exists():
        raise SystemExit(f"input does not exist: {args.input}")

    with args.input.open("r", encoding="utf-8") as fh:
        payload = json.load(fh)

    html = render_dashboard(payload)
    ensure_parent_dir(args.output)
    with args.output.open("w", encoding="utf-8") as fh:
        fh.write(html)

    print(f"wrote visualization to {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
