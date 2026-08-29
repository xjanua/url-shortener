import { expect, test } from "@playwright/test";

test("creates a link from the workspace spotlight", async ({ page }) => {
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
  await page.route("**/short-links", async (route) => {
    await route.fulfill({
      status: 201,
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: {
          id: 42,
          title: null,
          originalUrl: "https://example.com/a-very-long-path",
          shortUrl: "http://localhost:8080/r/a1b2c3",
          status: "ACTIVE",
          clickCount: 0,
          uniqueClicks: 0,
          createdAt: "2026-08-28T09:00:00",
        },
      }),
    });
  });

  await page.goto("/links");
  await page.getByRole("button", { name: "Tạo link" }).click();
  await page
    .getByLabel("URL cần rút gọn")
    .fill("https://example.com/a-very-long-path");
  await page.getByRole("button", { name: "Tạo link" }).click();

  await expect(
    page.getByText("http://localhost:8080/r/a1b2c3", { exact: true }),
  ).toBeVisible();
  await expect(page.getByRole("dialog")).toBeHidden();
});

test("creates a link with advanced options", async ({ page }) => {
  let submittedPayload: Record<string, string> = {};

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
  await page.route("**/short-links", async (route) => {
    submittedPayload = route.request().postDataJSON() as Record<string, string>;
    await route.fulfill({
      status: 201,
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: {
          id: 43,
          title: submittedPayload.title,
          originalUrl: submittedPayload.originalUrl,
          shortUrl: "http://localhost:8080/r/mua-he",
          status: "ACTIVE",
          clickCount: 0,
          uniqueClicks: 0,
          createdAt: "2026-08-28T09:00:00",
        },
      }),
    });
  });

  await page.goto("/links");
  await page.getByRole("button", { name: "Tạo link" }).click();
  await page.getByLabel("URL cần rút gọn").fill("https://example.com/campaign");
  await page.getByRole("button", { name: "Tùy chọn nâng cao" }).click();

  await expect(page.getByRole("dialog")).toBeVisible();
  await page.getByLabel("Tiêu đề").fill("Chiến dịch mùa hè");
  await page.getByLabel("Mã tùy chỉnh").fill("mua-he");
  await page.getByLabel("Mật khẩu").fill("secret123");
  await page.getByLabel("Thời hạn").fill("2030-08-28T12:00");
  await page.getByRole("button", { name: "Tạo liên kết" }).click();

  await expect(page.getByRole("dialog")).toBeHidden();
  await expect(
    page.getByText("http://localhost:8080/r/mua-he", { exact: true }),
  ).toBeVisible();
  expect(submittedPayload).toMatchObject({
    originalUrl: "https://example.com/campaign",
    title: "Chiến dịch mùa hè",
    shortCode: "mua-he",
    password: "secret123",
    expiresAt: "2030-08-28T12:00",
  });
  expect(submittedPayload).not.toHaveProperty("androidUrl");
});
