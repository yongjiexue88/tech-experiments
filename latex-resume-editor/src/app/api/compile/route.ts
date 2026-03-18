import { spawn } from "node:child_process";
import { access, mkdtemp, mkdir, readFile, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import path from "node:path";
import JSZip from "jszip";
import { NextResponse } from "next/server";
import type { CompileEngine, CompileResponse } from "@/lib/types";

export const runtime = "nodejs";
export const dynamic = "force-dynamic";

const TEXLIVE_IMAGE = process.env.TEXLIVE_IMAGE ?? "texlive/texlive:latest";
const TEXLIVE_DOCKER_PLATFORM = process.env.TEXLIVE_DOCKER_PLATFORM?.trim() ?? "";
const parsedTimeout = Number(process.env.COMPILE_TIMEOUT_MS ?? "300000");
const COMPILE_TIMEOUT_MS = Number.isFinite(parsedTimeout) && parsedTimeout > 0 ? parsedTimeout : 300000;
const MAX_ZIP_SIZE_BYTES = 25 * 1024 * 1024;
const MAX_LOG_CHARS = 500_000;
const SUPPORTED_ENGINES = new Set<CompileEngine>(["xelatex"]);

interface ProcessRunResult {
  code: number | null;
  signal: NodeJS.Signals | null;
  output: string;
  timedOut: boolean;
  spawnError?: string;
}

function normalizeRelativePath(raw: string): string {
  const unixPath = raw.replace(/\\/g, "/").trim();
  const normalized = path.posix.normalize(unixPath).replace(/^\.\//, "");

  if (
    normalized === "" ||
    normalized.startsWith("/") ||
    normalized.startsWith("../") ||
    normalized.includes("/../")
  ) {
    throw new Error(`Invalid path: ${raw}`);
  }

  return normalized;
}

async function unpackZipToDirectory(zipBuffer: Buffer, workDir: string): Promise<string[]> {
  const zip = await JSZip.loadAsync(zipBuffer);
  const writtenPaths: string[] = [];

  const entries = Object.entries(zip.files).sort(([a], [b]) => a.localeCompare(b));
  for (const [entryPath, entry] of entries) {
    if (entry.dir) {
      continue;
    }

    const relativePath = normalizeRelativePath(entryPath);
    const outPath = path.join(workDir, relativePath);
    const dirPath = path.dirname(outPath);

    await mkdir(dirPath, { recursive: true });
    const bytes = await entry.async("uint8array");
    await writeFile(outPath, Buffer.from(bytes));
    writtenPaths.push(relativePath);
  }

  return writtenPaths;
}

function shellEscapeSingleQuotes(input: string): string {
  return `'${input.replace(/'/g, `'"'"'`)}'`;
}

function compileCommand(engine: CompileEngine, entrypoint: string): string {
  const escapedEntrypoint = shellEscapeSingleQuotes(entrypoint);
  const compileFlags = "-interaction=nonstopmode -halt-on-error -file-line-error";
  return `${engine} ${compileFlags} ${escapedEntrypoint} && ${engine} ${compileFlags} ${escapedEntrypoint}`;
}

function runDockerCompile(workDir: string, engine: CompileEngine, entrypoint: string): Promise<ProcessRunResult> {
  const args = [
    "run",
    "--rm",
  ];

  if (TEXLIVE_DOCKER_PLATFORM) {
    args.push("--platform", TEXLIVE_DOCKER_PLATFORM);
  }

  args.push(
    "-v",
    `${workDir}:/workdir`,
    "-w",
    "/workdir",
    TEXLIVE_IMAGE,
    "/bin/sh",
    "-lc",
    compileCommand(engine, entrypoint),
  );

  return new Promise((resolve) => {
    const child = spawn("docker", args, {
      stdio: ["ignore", "pipe", "pipe"],
    });

    let output = "";
    let timedOut = false;
    let spawnError: string | undefined;

    const appendOutput = (chunk: Buffer) => {
      if (output.length >= MAX_LOG_CHARS) {
        return;
      }
      const remaining = MAX_LOG_CHARS - output.length;
      output += chunk.toString("utf-8").slice(0, remaining);
    };

    child.stdout?.on("data", (chunk: Buffer) => appendOutput(chunk));
    child.stderr?.on("data", (chunk: Buffer) => appendOutput(chunk));

    child.on("error", (error) => {
      spawnError = error.message;
    });

    const timeoutId = setTimeout(() => {
      timedOut = true;
      child.kill("SIGKILL");
    }, COMPILE_TIMEOUT_MS);

    child.on("close", (code, signal) => {
      clearTimeout(timeoutId);
      resolve({
        code,
        signal,
        output,
        timedOut,
        spawnError,
      });
    });
  });
}

function latexErrorsFromLog(log: string): string[] {
  const lines = log.split(/\r?\n/);
  const errors: string[] = [];

  for (let i = 0; i < lines.length; i += 1) {
    const line = lines[i]?.trim();
    if (!line || !line.startsWith("!")) {
      continue;
    }

    const context = [line, lines[i + 1]?.trim(), lines[i + 2]?.trim()]
      .filter(Boolean)
      .join(" ");
    errors.push(context);

    if (errors.length >= 8) {
      break;
    }
  }

  return errors;
}

function badRequest(message: string) {
  return NextResponse.json({ success: false, message }, { status: 400 });
}

export async function POST(request: Request) {
  const startedAt = Date.now();
  let tempDir = "";

  try {
    const formData = await request.formData();
    const zipField = formData.get("projectZip");
    const entrypointField = formData.get("entrypoint");
    const engineField = formData.get("engine");

    if (!(zipField instanceof File)) {
      return badRequest("Missing projectZip file in multipart form data.");
    }

    if (typeof entrypointField !== "string" || entrypointField.trim() === "") {
      return badRequest("Missing entrypoint path.");
    }

    if (typeof engineField !== "string" || !SUPPORTED_ENGINES.has(engineField as CompileEngine)) {
      return badRequest("Unsupported compile engine. Only xelatex is supported.");
    }

    const entrypoint = normalizeRelativePath(entrypointField);
    if (!entrypoint.toLowerCase().endsWith(".tex")) {
      return badRequest("Entrypoint must be a .tex file.");
    }

    if (zipField.size > MAX_ZIP_SIZE_BYTES) {
      return badRequest(`projectZip exceeds ${MAX_ZIP_SIZE_BYTES / (1024 * 1024)}MB limit.`);
    }

    const zipBuffer = Buffer.from(await zipField.arrayBuffer());
    tempDir = await mkdtemp(path.join(tmpdir(), "latex-resume-editor-"));
    const writtenPaths = await unpackZipToDirectory(zipBuffer, tempDir);

    if (!writtenPaths.includes(entrypoint)) {
      return badRequest(`Entrypoint ${entrypoint} was not found in project zip.`);
    }

    await access(path.join(tempDir, entrypoint));

    const processResult = await runDockerCompile(tempDir, engineField as CompileEngine, entrypoint);
    const durationMs = Date.now() - startedAt;

    if (processResult.spawnError) {
      const response: CompileResponse = {
        success: false,
        log: processResult.output,
        errors: [`Docker execution failed: ${processResult.spawnError}`],
        durationMs,
      };
      return NextResponse.json(response);
    }

    if (processResult.timedOut) {
      const response: CompileResponse = {
        success: false,
        log: processResult.output,
        errors: [`Compile timed out after ${COMPILE_TIMEOUT_MS / 1000}s.`],
        durationMs,
      };
      return NextResponse.json(response);
    }

    if (processResult.code !== 0) {
      const lowerLog = processResult.output.toLowerCase();
      if (lowerLog.includes("no matching manifest for")) {
        const response: CompileResponse = {
          success: false,
          log: processResult.output,
          errors: [
            "Docker image architecture mismatch. Use an arm64-compatible TeX image or set TEXLIVE_DOCKER_PLATFORM=linux/amd64.",
          ],
          durationMs,
        };
        return NextResponse.json(response);
      }

      const parsedErrors = latexErrorsFromLog(processResult.output);
      const response: CompileResponse = {
        success: false,
        log: processResult.output,
        errors:
          parsedErrors.length > 0
            ? parsedErrors
            : [`LaTeX compile failed (exit code ${processResult.code ?? "unknown"}).`],
        durationMs,
      };
      return NextResponse.json(response);
    }

    const pdfPath = path.join(tempDir, entrypoint.replace(/\.tex$/i, ".pdf"));
    const pdfBuffer = await readFile(pdfPath);

    const response: CompileResponse = {
      success: true,
      pdfBase64: pdfBuffer.toString("base64"),
      log: processResult.output,
      durationMs,
    };
    return NextResponse.json(response);
  } catch (error) {
    const message = error instanceof Error ? error.message : "Unknown server error.";
    const response: CompileResponse = {
      success: false,
      log: "",
      errors: [message],
      durationMs: Date.now() - startedAt,
    };
    return NextResponse.json(response, { status: 500 });
  } finally {
    if (tempDir) {
      await rm(tempDir, { recursive: true, force: true });
    }
  }
}
