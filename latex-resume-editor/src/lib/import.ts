import JSZip from "jszip";
import { normalizeProjectPath, uint8ArrayToBase64 } from "./project-zip";
import type { ProjectFile, ProjectState } from "./types";

const TEXT_EXTENSIONS = new Set([
  ".tex",
  ".bib",
  ".sty",
  ".cls",
  ".txt",
  ".md",
  ".csv",
  ".json",
  ".yaml",
  ".yml",
  ".xml",
  ".bst",
  ".blg",
  ".log",
  ".aux",
  ".toc",
]);

function extensionOf(path: string): string {
  const lastDot = path.lastIndexOf(".");
  if (lastDot < 0) {
    return "";
  }
  return path.slice(lastDot).toLowerCase();
}

function isLikelyBinary(bytes: Uint8Array): boolean {
  if (bytes.length === 0) {
    return false;
  }

  let suspiciousBytes = 0;
  const sampleSize = Math.min(bytes.length, 4096);
  for (let i = 0; i < sampleSize; i += 1) {
    const value = bytes[i];
    if (value === 0) {
      return true;
    }
    const isControl = value < 9 || (value > 13 && value < 32);
    if (isControl) {
      suspiciousBytes += 1;
    }
  }

  return suspiciousBytes / sampleSize > 0.2;
}

function decodeUtf8(bytes: Uint8Array): string {
  const decoder = new TextDecoder("utf-8", { fatal: false });
  return decoder.decode(bytes);
}

function detectEntrypoint(files: ProjectFile[]): string {
  const texFiles = files
    .filter((file) => !file.isBinary && file.path.toLowerCase().endsWith(".tex"))
    .map((file) => file.path)
    .sort((a, b) => a.localeCompare(b));

  if (texFiles.length === 0) {
    throw new Error("No .tex file found in the imported project.");
  }

  const explicitMain = texFiles.find((path) => path.toLowerCase() === "main.tex");
  if (explicitMain) {
    return explicitMain;
  }

  const nestedMain = texFiles.find((path) => path.toLowerCase().endsWith("/main.tex"));
  if (nestedMain) {
    return nestedMain;
  }

  return texFiles[0];
}

function buildProjectState(files: ProjectFile[]): ProjectState {
  return {
    files,
    entrypoint: detectEntrypoint(files),
    engine: "xelatex",
  };
}

async function importTexFile(file: File): Promise<ProjectState> {
  const path = normalizeProjectPath(file.name || "main.tex");
  if (!path.toLowerCase().endsWith(".tex")) {
    throw new Error("Imported file must end with .tex");
  }

  const content = await file.text();
  return {
    files: [
      {
        path,
        content,
        isBinary: false,
      },
    ],
    entrypoint: path,
    engine: "xelatex",
  };
}

async function importZipFile(file: File): Promise<ProjectState> {
  const arrayBuffer = await file.arrayBuffer();
  const zip = await JSZip.loadAsync(arrayBuffer);
  const paths = Object.keys(zip.files).sort((a, b) => a.localeCompare(b));

  const projectFiles: ProjectFile[] = [];
  for (const rawPath of paths) {
    const entry = zip.files[rawPath];
    if (!entry || entry.dir) {
      continue;
    }

    const path = normalizeProjectPath(rawPath);
    const bytes = await entry.async("uint8array");
    const ext = extensionOf(path);
    const shouldTreatAsText = TEXT_EXTENSIONS.has(ext) || !isLikelyBinary(bytes);

    if (shouldTreatAsText) {
      projectFiles.push({
        path,
        content: decodeUtf8(bytes),
        isBinary: false,
      });
      continue;
    }

    projectFiles.push({
      path,
      content: "",
      binaryBase64: uint8ArrayToBase64(bytes),
      isBinary: true,
    });
  }

  if (projectFiles.length === 0) {
    throw new Error("The imported zip does not contain any usable files.");
  }

  return buildProjectState(projectFiles);
}

export async function importProjectFile(file: File): Promise<ProjectState> {
  const lowerName = file.name.toLowerCase();

  if (lowerName.endsWith(".tex")) {
    return importTexFile(file);
  }

  if (lowerName.endsWith(".zip")) {
    return importZipFile(file);
  }

  throw new Error("Unsupported file type. Please import a .tex or .zip file.");
}
