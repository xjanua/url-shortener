import { apiRequest } from "@/lib/api/client";
import type { CreateLinkInput } from "../schemas/link-schema";
import type {
  LinkStats,
  PaginatedLinks,
  ShortLink,
  ShortLinkStatus,
} from "../types/link";

export function getRecentLinks() {
  return apiRequest<PaginatedLinks>(
    "/short-links?page=0&size=5&sort=createdAt,desc",
  );
}

export function getLinks(size = 20) {
  return apiRequest<PaginatedLinks>(
    `/short-links?page=0&size=${size}&sort=createdAt,desc`,
  );
}

export function getLinkStats(id: number) {
  return apiRequest<LinkStats>(`/short-links/${id}/stats`);
}

export function updateLinkStatus(id: number, status: ShortLinkStatus) {
  return apiRequest<ShortLink>(`/short-links/${id}/status`, {
    method: "PUT",
    body: JSON.stringify({ status }),
  });
}

export function createLink(values: CreateLinkInput) {
  const payload = Object.fromEntries(
    Object.entries(values).filter(
      ([, value]) => value !== "" && value !== undefined,
    ),
  );
  return apiRequest<ShortLink>("/short-links", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}
