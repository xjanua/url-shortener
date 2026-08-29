import { expect, test } from "@playwright/test";

test("login validates input and handles a successful response", async ({
  page,
}) => {
  await page.route("**/auth/login", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: { accessToken: "test-token" },
      }),
    });
  });
  await page.route("**/short-links?*", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: {
          info: { page: 0, size: 5, pages: 0, total: 0 },
          response: [],
        },
      }),
    });
  });
  await page.route("**/dashboard/summary", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: {
          totalShortLinks: 0,
          shortLinksCreatedToday: 0,
          totalClicks: 0,
          clicksToday: 0,
        },
      }),
    });
  });
  await page.route("**/dashboard/daily-clicks?*", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ success: true, error: null, data: [] }),
    });
  });

  await page.goto("/login");
  await page.getByRole("button", { name: "Đăng nhập" }).click();

  await expect(page.getByText("Vui lòng nhập email.")).toBeVisible();
  await expect(page.getByText("Vui lòng nhập mật khẩu.")).toBeVisible();

  await page.getByLabel("Email").fill("owner@example.com");
  await page.getByLabel("Mật khẩu", { exact: true }).fill("password123");
  const passwordToggle = page.getByRole("button", { name: "Hiện mật khẩu" });
  await passwordToggle.focus();
  await passwordToggle.press("Enter");
  await expect(page.getByLabel("Mật khẩu", { exact: true })).toHaveAttribute(
    "type",
    "text",
  );

  await page.getByRole("button", { name: "Đăng nhập" }).click();
  await expect(page).toHaveURL(/\/dashboard$/);
  await expect(
    page.getByRole("heading", { name: /Chào buổi chiều, Toàn/ }),
  ).toBeVisible();
});

test("register validates matching passwords and links back to login", async ({
  page,
}) => {
  await page.goto("/register");
  await page.getByLabel("Email").fill("owner@example.com");
  await page.getByLabel("Mật khẩu", { exact: true }).fill("password123");
  await page.getByLabel("Xác nhận mật khẩu").fill("different123");
  await page.getByRole("button", { name: "Tạo tài khoản" }).click();

  await expect(page.getByText("Mật khẩu xác nhận không khớp.")).toBeVisible();
  await expect(page.getByRole("link", { name: "Đăng nhập" })).toHaveAttribute(
    "href",
    "/login",
  );
});
