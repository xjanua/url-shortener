import type { Metadata } from "next";

import { AnalyticsOverview } from "@/features/analytics";

export const metadata: Metadata = { title: "Phân tích" };

export default function AnalyticsPage() {
  return <AnalyticsOverview />;
}
