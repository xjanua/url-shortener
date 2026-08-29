import { apiRequest } from "@/lib/api/client";
import type {
  DailyClick,
  DashboardLinksResponse,
  DashboardSummary,
} from "../types/dashboard";

export function getDashboardData(from: string, to: string) {
  return Promise.all([
    apiRequest<DashboardSummary>("/dashboard/summary"),
    apiRequest<DailyClick[]>(`/dashboard/daily-clicks?from=${from}&to=${to}`),
    apiRequest<DashboardLinksResponse>(
      "/short-links?page=0&size=3&sort=createdAt,desc",
    ),
  ]);
}
