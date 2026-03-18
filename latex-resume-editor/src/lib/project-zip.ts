import JSZip from "jszip";
import type { ProjectState } from "./types";

export function normalizeProjectPath(input: string): string {
  const unixPath = input.replace(/\\/g, "/");
  const collapsed = unixPath
    .split("/")
    .filter((part) => part.length > 0 && part !== ".")
    .join("/");

  if (collapsed === "" || collapsed.startsWith("..") || collapsed.includes("/../")) {
    throw new Error(`Invalid project path: ${input}`);
  }

  return collapsed;
}

export function uint8ArrayToBase64(bytes: Uint8Array): string {
  if (typeof Buffer !== "undefined") {
    return Buffer.from(bytes).toString("base64");
  }

  let binary = "";
  const chunkSize = 0x8000;
  for (let i = 0; i < bytes.length; i += chunkSize) {
    const chunk = bytes.subarray(i, i + chunkSize);
    binary += String.fromCharCode(...chunk);
  }
  return btoa(binary);
}

export function base64ToUint8Array(base64: string): Uint8Array {
  if (typeof Buffer !== "undefined") {
    return Uint8Array.from(Buffer.from(base64, "base64"));
  }

  const binary = atob(base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i += 1) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes;
}

export function base64ToArrayBuffer(base64: string): ArrayBuffer {
  const bytes = base64ToUint8Array(base64);
  const arrayBuffer = new ArrayBuffer(bytes.byteLength);
  new Uint8Array(arrayBuffer).set(bytes);
  return arrayBuffer;
}

export async function createProjectZipBlob(project: ProjectState): Promise<Blob> {
  const zip = new JSZip();

  for (const file of project.files) {
    const path = normalizeProjectPath(file.path);
    if (file.isBinary) {
      if (!file.binaryBase64) {
        throw new Error(`Missing binary payload for ${path}`);
      }
      zip.file(path, base64ToUint8Array(file.binaryBase64));
      continue;
    }

    zip.file(path, file.content ?? "");
  }

  return zip.generateAsync({ type: "blob" });
}
