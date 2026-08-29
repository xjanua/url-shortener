import { afterEach, describe, expect, it, vi } from "vitest";

import { API_BASE_URL, apiRequest } from "./client";

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("apiRequest", () => {
  it("calls Spring directly and includes credentials", async () => {
    const fetchMock = vi
      .fn()
      .mockResolvedValue(
        new Response(
          JSON.stringify({ success: true, error: null, data: { id: "1" } }),
          { status: 200, headers: { "Content-Type": "application/json" } },
        ),
      );
    vi.stubGlobal("fetch", fetchMock);

    const result = await apiRequest<{ id: string }>("/links");

    expect(result).toEqual({ success: true, data: { id: "1" } });
    expect(fetchMock).toHaveBeenCalledWith(
      `${API_BASE_URL}/links`,
      expect.objectContaining({ credentials: "include" }),
    );
  });

  it("normalizes backend business errors", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response(
          JSON.stringify({
            success: false,
            error: { code: "LINK_NOT_FOUND", message: "Không tìm thấy link." },
            data: null,
          }),
          { status: 404, headers: { "Content-Type": "application/json" } },
        ),
      ),
    );

    await expect(apiRequest("/links/missing")).resolves.toEqual({
      success: false,
      error: { code: "LINK_NOT_FOUND", message: "Không tìm thấy link." },
    });
  });
});
