import type { ProjectFile } from "@/lib/types";

interface FileTreeProps {
  files: ProjectFile[];
  selectedPath: string | null;
  entrypoint: string;
  onSelect: (path: string) => void;
  onSetEntrypoint: (path: string) => void;
}

function pathDepth(path: string): number {
  return Math.max(0, path.split("/").length - 1);
}

function isTexFile(path: string): boolean {
  return path.toLowerCase().endsWith(".tex");
}

export function FileTree({
  files,
  selectedPath,
  entrypoint,
  onSelect,
  onSetEntrypoint,
}: FileTreeProps) {
  const sortedFiles = [...files].sort((a, b) => a.path.localeCompare(b.path));

  return (
    <aside className="lr-panel lr-filetree">
      <div className="lr-panel-header">
        <h2>Project Files</h2>
        <span>{files.length}</span>
      </div>
      <ul className="lr-file-list">
        {sortedFiles.map((file) => {
          const selected = file.path === selectedPath;
          const texFile = isTexFile(file.path);
          const isEntrypoint = texFile && file.path === entrypoint;
          const depth = pathDepth(file.path);

          return (
            <li key={file.path} className="lr-file-row">
              <button
                type="button"
                className={`lr-file-button ${selected ? "is-selected" : ""}`}
                onClick={() => onSelect(file.path)}
                style={{ paddingLeft: `${12 + depth * 16}px` }}
                title={file.path}
              >
                <span className={`lr-tag ${file.isBinary ? "is-binary" : "is-text"}`}>
                  {file.isBinary ? "BIN" : "TXT"}
                </span>
                <span className="lr-file-path">{file.path}</span>
              </button>
              {texFile ? (
                <button
                  type="button"
                  className={`lr-entry-button ${isEntrypoint ? "is-entry" : ""}`}
                  onClick={() => onSetEntrypoint(file.path)}
                  title="Set as LaTeX entrypoint"
                >
                  {isEntrypoint ? "Entry" : "Set"}
                </button>
              ) : null}
            </li>
          );
        })}
      </ul>
    </aside>
  );
}
