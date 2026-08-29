import { expect, test } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.route("**/dashboard/summary", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: {
          totalShortLinks: 128,
          shortLinksCreatedToday: 6,
          totalClicks: 24521,
          clicksToday: 842,
        },
      }),
    }),
  );
  await page.route("**/dashboard/daily-clicks?*", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: [
          { day: "2026-08-22", count: 210 },
          { day: "2026-08-23", count: 480 },
          { day: "2026-08-24", count: 390 },
          { day: "2026-08-25", count: 720 },
          { day: "2026-08-26", count: 610 },
          { day: "2026-08-27", count: 920 },
          { day: "2026-08-28", count: 842 },
        ],
      }),
    }),
  );
  await page.route("**/short-links?*", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: {
          info: { page: 0, size: 3, pages: 1, total: 3 },
          response: [
            {
              id: 1,
              title: "Facebook campaign",
              originalUrl: "https://facebook.com/campaign",
              shortUrl: "https://sho.rt/facebook",
              clickCount: 8421,
              createdAt: "2026-08-28T09:00:00",
            },
            {
              id: 2,
              title: "GitHub project",
              originalUrl: "https://github.com/project",
              shortUrl: "https://sho.rt/github",
              clickCount: 5128,
              createdAt: "2026-08-27T09:00:00",
            },
            {
              id: 3,
              title: "Portfolio",
              originalUrl: "https://example.com/portfolio",
              shortUrl: "https://sho.rt/portfolio",
              clickCount: 3924,
              createdAt: "2026-08-26T09:00:00",
            },
          ],
        },
      }),
    }),
  );
});

test("shows real dashboard metrics, activity and recent links", async ({
  page,
}) => {
  await page.goto("/dashboard");
  await expect(
    page.getByRole("heading", { name: /Chào buổi chiều, Toàn/ }),
  ).toBeVisible();
  await expect(page.getByText("24.521", { exact: true })).toBeVisible();
  await expect(page.getByText("Facebook campaign")).toBeVisible();
  await expect(
    page.getByRole("link", { name: "Tạo liên kết ngắn" }),
  ).toHaveAttribute("href", "/links");
  await expect(page.getByLabel("Biểu đồ lượt nhấp trong 7 ngày")).toBeVisible();
});
