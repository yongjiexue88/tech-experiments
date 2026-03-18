"use client";

import { ChangeEvent, useCallback, useEffect, useMemo, useRef, useState } from "react";
import { EditorPane } from "@/components/EditorPane";
import { FileTree } from "@/components/FileTree";
import { PdfPreview } from "@/components/PdfPreview";
import { importProjectFile } from "@/lib/import";
import { base64ToArrayBuffer, createProjectZipBlob } from "@/lib/project-zip";
import type { CompileResponse, ProjectState } from "@/lib/types";

const AUTO_COMPILE_DEBOUNCE_MS = 1200;

function downloadBlob(blob: Blob, filename: string): void {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement("a");
  anchor.href = url;
  anchor.download = filename;
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  URL.revokeObjectURL(url);
}

export default function Home() {
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const projectRef = useRef<ProjectState | null>(null);
  const compileRunningRef = useRef(false);
  const compileQueuedRef = useRef(false);

  const [project, setProject] = useState<ProjectState | null>(null);
  const [selectedPath, setSelectedPath] = useState<string | null>(null);
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [compileResult, setCompileResult] = useState<CompileResponse | null>(null);
  const [isCompiling, setIsCompiling] = useState(false);
  const [autoCompile, setAutoCompile] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string>("Import a .tex or .zip project to begin.");
  const [revision, setRevision] = useState(0);

  useEffect(() => {
    projectRef.current = project;
  }, [project]);

  useEffect(() => {
    return () => {
      setPdfUrl((previous) => {
        if (previous) {
          URL.revokeObjectURL(previous);
        }
        return null;
      });
    };
  }, []);

  const selectedFile = useMemo(() => {
    if (!project || !selectedPath) {
      return null;
    }
    return project.files.find((file) => file.path === selectedPath) ?? null;
  }, [project, selectedPath]);

  const texFiles = useMemo(() => {
    if (!project) {
      return [];
    }

    return project.files
      .filter((file) => !file.isBinary && file.path.toLowerCase().endsWith(".tex"))
      .map((file) => file.path)
      .sort((a, b) => a.localeCompare(b));
  }, [project]);

  const runCompile = useCallback(async (trigger: "manual" | "auto" | "queued" = "manual") => {
    const currentProject = projectRef.current;
    if (!currentProject) {
      setStatusMessage("Import a project first.");
      return;
    }

    if (compileRunningRef.current) {
      compileQueuedRef.current = true;
      return;
    }

    compileRunningRef.current = true;
    setIsCompiling(true);

    do {
      compileQueuedRef.current = false;
      const snapshot = projectRef.current;
      if (!snapshot) {
        break;
      }

      try {
        const zipBlob = await createProjectZipBlob(snapshot);
        const formData = new FormData();
        formData.set(
          "projectZip",
          new File([zipBlob], "project.zip", {
            type: "application/zip",
          }),
        );
        formData.set("entrypoint", snapshot.entrypoint);
        formData.set("engine", snapshot.engine);

        setStatusMessage(
          trigger === "auto"
            ? "Auto-compiling project..."
            : trigger === "queued"
              ? "Compiling queued changes..."
              : "Compiling project...",
        );

        const response = await fetch("/api/compile", {
          method: "POST",
          body: formData,
        });

        const payload = (await response.json()) as CompileResponse & { message?: string };
        if (!response.ok && payload.message) {
          throw new Error(payload.message);
        }

        setCompileResult(payload);

        if (payload.success && payload.pdfBase64) {
          const pdfBlob = new Blob([base64ToArrayBuffer(payload.pdfBase64)], {
            type: "application/pdf",
          });
          const nextUrl = URL.createObjectURL(pdfBlob);
          setPdfUrl((previous) => {
            if (previous) {
              URL.revokeObjectURL(previous);
            }
            return nextUrl;
          });
          setStatusMessage(`Compile succeeded in ${(payload.durationMs / 1000).toFixed(2)}s.`);
        } else {
          setStatusMessage("Compile finished with errors. Check compile log.");
        }
      } catch (error) {
        const message = error instanceof Error ? error.message : "Compile request failed.";
        setCompileResult({
          success: false,
          log: "",
          errors: [message],
          durationMs: 0,
        });
        setStatusMessage(message);
      }
    } while (compileQueuedRef.current);

    compileRunningRef.current = false;
    setIsCompiling(false);
  }, []);

  const handleImportClick = useCallback(() => {
    fileInputRef.current?.click();
  }, []);

  const handleImportChange = useCallback(async (event: ChangeEvent<HTMLInputElement>) => {
    const nextFile = event.target.files?.[0];
    event.target.value = "";

    if (!nextFile) {
      return;
    }

    try {
      const importedProject = await importProjectFile(nextFile);
      setProject(importedProject);
      setSelectedPath(importedProject.entrypoint);
      setCompileResult(null);
      setStatusMessage(`Imported ${nextFile.name}.`);
      setRevision((value) => value + 1);
      setPdfUrl((previous) => {
        if (previous) {
          URL.revokeObjectURL(previous);
        }
        return null;
      });
    } catch (error) {
      const message = error instanceof Error ? error.message : "Failed to import project.";
      setStatusMessage(message);
      setCompileResult({
        success: false,
        log: "",
        errors: [message],
        durationMs: 0,
      });
    }
  }, []);

  const updateFileContent = useCallback((path: string, value: string) => {
    setProject((previous) => {
      if (!previous) {
        return previous;
      }

      const files = previous.files.map((file) =>
        file.path === path ? { ...file, content: value } : file,
      );

      return {
        ...previous,
        files,
      };
    });
    setRevision((current) => current + 1);
  }, []);

  const setEntrypoint = useCallback((path: string) => {
    setProject((previous) => {
      if (!previous) {
        return previous;
      }
      return {
        ...previous,
        entrypoint: path,
      };
    });
    setSelectedPath(path);
    setRevision((current) => current + 1);
  }, []);

  const handleEntrypointSelect = useCallback((event: ChangeEvent<HTMLSelectElement>) => {
    setEntrypoint(event.target.value);
  }, [setEntrypoint]);

  const exportPdf = useCallback(() => {
    if (!compileResult?.pdfBase64) {
      return;
    }

    const blob = new Blob([base64ToArrayBuffer(compileResult.pdfBase64)], {
      type: "application/pdf",
    });
    downloadBlob(blob, "resume.pdf");
  }, [compileResult]);

  const exportSourceZip = useCallback(async () => {
    if (!project) {
      return;
    }

    const zipBlob = await createProjectZipBlob(project);
    downloadBlob(zipBlob, "latex-project.zip");
  }, [project]);

  useEffect(() => {
    if (!autoCompile || !project) {
      return;
    }

    const timerId = window.setTimeout(() => {
      void runCompile("auto");
    }, AUTO_COMPILE_DEBOUNCE_MS);

    return () => {
      window.clearTimeout(timerId);
    };
  }, [autoCompile, project, revision, runCompile]);

  return (
    <div className="lr-root">
      <header className="lr-header">
        <div>
          <p className="lr-eyebrow">Local Resume Builder</p>
          <h1>LaTeX Resume Editor</h1>
          <p className="lr-status">{statusMessage}</p>
        </div>
        <div className="lr-actions">
          <input
            ref={fileInputRef}
            type="file"
            accept=".tex,.zip,application/zip,application/x-zip-compressed"
            onChange={handleImportChange}
            hidden
          />
          <button type="button" className="lr-btn" onClick={handleImportClick}>
            Import .tex/.zip
          </button>
          <button
            type="button"
            className="lr-btn lr-btn-primary"
            onClick={() => void runCompile("manual")}
            disabled={!project || isCompiling}
          >
            Compile PDF
          </button>
          <button
            type="button"
            className="lr-btn"
            onClick={exportPdf}
            disabled={!compileResult?.success || !compileResult?.pdfBase64}
          >
            Export PDF
          </button>
          <button type="button" className="lr-btn" onClick={() => void exportSourceZip()} disabled={!project}>
            Export Source ZIP
          </button>
        </div>
      </header>

      <section className="lr-toolbar">
        <label className="lr-inline-field" htmlFor="entrypoint-select">
          Entrypoint
          <select
            id="entrypoint-select"
            value={project?.entrypoint ?? ""}
            onChange={handleEntrypointSelect}
            disabled={!project || texFiles.length === 0}
          >
            {texFiles.map((path) => (
              <option key={path} value={path}>
                {path}
              </option>
            ))}
          </select>
        </label>

        <label className="lr-inline-toggle" htmlFor="auto-compile">
          <input
            id="auto-compile"
            type="checkbox"
            checked={autoCompile}
            onChange={(event) => setAutoCompile(event.target.checked)}
            disabled={!project}
          />
          Auto compile ({AUTO_COMPILE_DEBOUNCE_MS}ms)
        </label>
      </section>

      <main className="lr-grid">
        <FileTree
          files={project?.files ?? []}
          selectedPath={selectedPath}
          entrypoint={project?.entrypoint ?? ""}
          onSelect={setSelectedPath}
          onSetEntrypoint={setEntrypoint}
        />

        <EditorPane file={selectedFile} onChange={updateFileContent} />

        <div className="lr-right-column">
          <PdfPreview
            pdfUrl={pdfUrl}
            isCompiling={isCompiling}
            durationMs={compileResult?.durationMs ?? null}
          />
          <section className="lr-panel lr-log-panel">
            <div className="lr-panel-header">
              <h2>Compile Log</h2>
              <span>{compileResult?.success ? "Success" : "Idle/Failed"}</span>
            </div>
            <pre className="lr-log-output">{compileResult?.log || "No compile output yet."}</pre>
            {compileResult?.errors?.length ? (
              <ul className="lr-error-list">
                {compileResult.errors.map((error) => (
                  <li key={error}>{error}</li>
                ))}
              </ul>
            ) : null}
          </section>
        </div>
      </main>
    </div>
  );
}
