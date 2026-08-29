import type { Metadata } from "next";

import { LinkWorkspace } from "@/features/links";

export const metadata: Metadata = { title: "Liên kết" };

export default function LinksPage() {
  return <LinkWorkspace />;
}
