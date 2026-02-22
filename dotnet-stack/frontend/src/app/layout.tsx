import type { Metadata } from "next";
import "../styles/globals.css";

export const metadata: Metadata = {
  title: "DotNet Stack Experiment",
  description: "A premium .NET 8 + Next.js template",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <main>{children}</main>
      </body>
    </html>
  );
}
