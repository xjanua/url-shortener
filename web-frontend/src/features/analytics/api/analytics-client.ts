import { apiRequest } from "@/lib/api/client";
import type { DailyClick, DashboardSummary } from "../types/analytics";

export function getDashboardSummary() {
  return apiRequest<DashboardSummary>("/dashboard/summary");
}

export function getDailyClicks(from: string, to: string) {
  return apiRequest<DailyClick[]>(
    `/dashboard/daily-clicks?from=${from}&to=${to}`,
  );
}
