import type { Metadata } from "next";

import { DashboardOverview } from "@/features/dashboard";

export const metadata: Metadata = { title: "Dashboard" };

export default function DashboardPage() {
  return <DashboardOverview />;
}
