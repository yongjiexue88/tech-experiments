# LaTeX Resume Editor

Local-first LaTeX editor for resume workflows.

## Features

- Import local `.tex` files or Overleaf-style `.zip` exports.
- Multi-file project editing with asset preservation.
- Select `main.tex` entrypoint when needed.
- Compile with Docker + `xelatex` (2 passes).
- Two-pane UX: code editor + PDF preview.
- Manual compile and optional debounced auto-compile.
- Export compiled PDF and source ZIP.

## Tech Stack

- Next.js App Router
- Monaco Editor (`@monaco-editor/react`)
- JSZip for import/export packaging
- Dockerized TeX Live for compile

## Prerequisites

- Node.js + npm
- Docker Desktop running

## Quick Start

```bash
cd /Users/yongjiexue/Documents/GitHub/tech-experiments/latex-resume-editor
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000).

## Environment

Create `.env.local` if you want a custom TeX image:

```bash
TEXLIVE_IMAGE=texlive/texlive:latest
# Optional architecture override (example):
# TEXLIVE_DOCKER_PLATFORM=linux/amd64
# Optional compile timeout (default 300000 ms):
# COMPILE_TIMEOUT_MS=300000
```

Default is already `texlive/texlive:latest`.

On first compile Docker may need to pull the TeX image, which can take several minutes.

## How Compile Works

`POST /api/compile` expects `multipart/form-data`:

- `projectZip`: zip bundle of current source files/assets
- `entrypoint`: e.g. `main.tex`
- `engine`: `xelatex`

The server route:

1. Unpacks source ZIP to a temp working directory.
2. Runs Docker compile:
   - `xelatex -interaction=nonstopmode -halt-on-error -file-line-error <entrypoint>`
   - Runs twice for refs/toc stability.
3. Returns JSON response:
   - `success`
   - `pdfBase64` (when successful)
   - `log`
   - `errors`
   - `durationMs`

## Supported Import Modes

- Single `.tex` file
- `.zip` project with nested folders and binary assets (`png`, `jpg`, fonts, etc.)

If no `main.tex` exists, first `.tex` file is selected by default and can be changed from the entrypoint selector.

## Notes

- This is local-only and single-user by design.
- No direct Overleaf API integration (import Overleaf export zip manually).
- No persistent backend storage; edits remain in browser memory unless exported.
