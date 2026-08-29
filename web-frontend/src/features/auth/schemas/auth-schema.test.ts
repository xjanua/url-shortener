import { describe, expect, it } from "vitest";

import { loginSchema, registerSchema } from "./auth-schema";

describe("loginSchema", () => {
  it("accepts a valid email and password", () => {
    expect(
      loginSchema.safeParse({
        email: "owner@example.com",
        password: "password123",
      }).success,
    ).toBe(true);
  });

  it("rejects an invalid email and short password", () => {
    const result = loginSchema.safeParse({
      email: "not-an-email",
      password: "short",
    });

    expect(result.success).toBe(false);
    if (!result.success) {
      expect(result.error.flatten().fieldErrors.email).toContain(
        "Email không đúng định dạng.",
      );
      expect(result.error.flatten().fieldErrors.password).toContain(
        "Mật khẩu cần có ít nhất 8 ký tự.",
      );
    }
  });
});

describe("registerSchema", () => {
  it("rejects password confirmation that does not match", () => {
    const result = registerSchema.safeParse({
      email: "owner@example.com",
      password: "password123",
      confirmPassword: "different123",
    });

    expect(result.success).toBe(false);
    if (!result.success) {
      expect(result.error.flatten().fieldErrors.confirmPassword).toContain(
        "Mật khẩu xác nhận không khớp.",
      );
    }
  });
});
