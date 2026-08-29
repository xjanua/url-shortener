import { describe, expect, it } from "vitest";

import { createLinkSchema } from "./link-schema";

describe("createLinkSchema", () => {
  it("accepts a complete URL", () => {
    expect(
      createLinkSchema.safeParse({ originalUrl: "https://example.com/path" })
        .success,
    ).toBe(true);
  });

  it("rejects an empty or malformed URL", () => {
    expect(createLinkSchema.safeParse({ originalUrl: "" }).success).toBe(false);
    expect(createLinkSchema.safeParse({ originalUrl: "example" }).success).toBe(
      false,
    );
  });

  it("accepts advanced options and validates a custom code", () => {
    expect(
      createLinkSchema.safeParse({
        originalUrl: "https://example.com/path",
        title: "Chiến dịch mùa hè",
        shortCode: "mua-he_2026",
        password: "secret123",
        expiresAt: "2030-08-28T12:00",
      }).success,
    ).toBe(true);

    expect(
      createLinkSchema.safeParse({
        originalUrl: "https://example.com/path",
        shortCode: "mã không hợp lệ",
      }).success,
    ).toBe(false);
  });
});
