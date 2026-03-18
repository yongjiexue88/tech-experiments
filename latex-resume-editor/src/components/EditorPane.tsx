import dynamic from "next/dynamic";
import type { ProjectFile } from "@/lib/types";

const MonacoEditor = dynamic(() => import("@monaco-editor/react"), {
  ssr: false,
  loading: () => <div className="lr-editor-loading">Loading editor...</div>,
});

interface EditorPaneProps {
  file: ProjectFile | null;
  onChange: (path: string, value: string) => void;
}

function languageForPath(path: string): string {
  const lower = path.toLowerCase();
  if (lower.endsWith(".tex") || lower.endsWith(".sty") || lower.endsWith(".cls")) {
    return "latex";
  }
  if (lower.endsWith(".bib")) {
    return "plaintext";
  }
  if (lower.endsWith(".json")) {
    return "json";
  }
  if (lower.endsWith(".md")) {
    return "markdown";
  }
  if (lower.endsWith(".xml")) {
    return "xml";
  }
  return "plaintext";
}

export function EditorPane({ file, onChange }: EditorPaneProps) {
  if (!file) {
    return (
      <section className="lr-panel lr-editor-panel">
        <div className="lr-panel-header">
          <h2>Editor</h2>
        </div>
        <div className="lr-editor-empty">Import a project and select a file to start editing.</div>
      </section>
    );
  }

  if (file.isBinary) {
    return (
      <section className="lr-panel lr-editor-panel">
        <div className="lr-panel-header">
          <h2>{file.path}</h2>
        </div>
        <div className="lr-editor-empty">
          This is a binary file. Editing is disabled, but the file is preserved for compile/export.
        </div>
      </section>
    );
  }

  return (
    <section className="lr-panel lr-editor-panel">
      <div className="lr-panel-header">
        <h2>{file.path}</h2>
      </div>
      <div className="lr-editor-surface">
        <MonacoEditor
          path={file.path}
          language={languageForPath(file.path)}
          value={file.content}
          onChange={(value) => onChange(file.path, value ?? "")}
          options={{
            minimap: { enabled: false },
            fontSize: 14,
            lineNumbersMinChars: 3,
            scrollBeyondLastLine: false,
            wordWrap: "on",
            automaticLayout: true,
          }}
          theme="vs-light"
        />
      </div>
    </section>
  );
}
