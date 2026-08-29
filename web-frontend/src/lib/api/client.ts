import type {
  ApiError,
  ClientApiResponse,
  RestResponse,
} from "@/lib/api/contracts";

const DEFAULT_API_URL = "http://localhost:8080";

export const API_BASE_URL = (
  process.env.NEXT_PUBLIC_API_URL ?? DEFAULT_API_URL
).replace(/\/$/, "");

const FALLBACK_ERROR: ApiError = {
  code: "API_ERROR",
  message: "Yêu cầu không thể hoàn tất. Vui lòng thử lại.",
};

export async function apiRequest<T>(
  path: string,
  init: RequestInit = {},
): Promise<ClientApiResponse<T>> {
  try {
    const response = await fetch(`${API_BASE_URL}${normalizePath(path)}`, {
      ...init,
      credentials: "include",
      headers: {
        Accept: "application/json",
        ...(init.body ? { "Content-Type": "application/json" } : {}),
        ...init.headers,
      },
    });

    const contentType = response.headers.get("content-type") ?? "";
    const body = contentType.includes("application/json")
      ? ((await response.json()) as RestResponse<T>)
      : null;

    if (!response.ok || !body?.success) {
      return {
        success: false,
        error: body?.error ?? FALLBACK_ERROR,
      };
    }

    return { success: true, data: body.data as T };
  } catch {
    return {
      success: false,
      error: {
        code: "CLIENT_NETWORK_ERROR",
        message: "Không thể kết nối máy chủ. Vui lòng thử lại.",
      },
    };
  }
}

function normalizePath(path: string) {
  return path.startsWith("/") ? path : `/${path}`;
}
