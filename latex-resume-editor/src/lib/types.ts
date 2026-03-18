export type CompileEngine = "xelatex";

export interface ProjectFile {
  path: string;
  content: string;
  binaryBase64?: string;
  isBinary: boolean;
}

export interface ProjectState {
  files: ProjectFile[];
  entrypoint: string;
  engine: CompileEngine;
}

export interface CompileResponse {
  success: boolean;
  pdfBase64?: string;
  log: string;
  errors?: string[];
  durationMs: number;
}
