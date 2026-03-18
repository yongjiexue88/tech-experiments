interface PdfPreviewProps {
  pdfUrl: string | null;
  isCompiling: boolean;
  durationMs: number | null;
}

export function PdfPreview({ pdfUrl, isCompiling, durationMs }: PdfPreviewProps) {
  return (
    <section className="lr-panel lr-preview-panel">
      <div className="lr-panel-header">
        <h2>PDF Preview</h2>
        {durationMs !== null ? <span>{(durationMs / 1000).toFixed(2)}s</span> : null}
      </div>
      <div className="lr-preview-surface">
        {isCompiling ? <div className="lr-compile-overlay">Compiling...</div> : null}
        {pdfUrl ? (
          <iframe title="Compiled PDF preview" src={pdfUrl} className="lr-preview-frame" />
        ) : (
          <div className="lr-preview-empty">Compile your LaTeX project to see PDF output.</div>
        )}
      </div>
    </section>
  );
}
