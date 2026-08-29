import type { Metadata } from "next";
import { notFound } from "next/navigation";

import { LinkDetail } from "@/features/links/components/link-detail";

export const metadata: Metadata = { title: "Chi tiết liên kết" };

export default async function LinkDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const numericId = Number(id);
  if (!Number.isInteger(numericId) || numericId < 1) notFound();
  return <LinkDetail id={numericId} />;
}
