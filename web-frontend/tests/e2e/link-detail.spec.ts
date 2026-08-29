import { expect, test } from "@playwright/test";

test("shows a detailed analytics view for a short link", async ({ page }) => {
  await page.route("**/short-links?*", (route) =>
    route.fulfill({
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: {
          info: { page: 0, size: 100, pages: 1, total: 1 },
          response: [
            {
              id: 2,
              title: "Facebook Campaign",
              originalUrl: "https://facebook.com/campaign",
              shortUrl: "https://sho.rt/facebook",
              status: "ACTIVE",
              clickCount: 24521,
              uniqueClicks: 18392,
              createdAt: "2026-08-28T09:00:00",
            },
          ],
        },
      }),
    }),
  );
  await page.route("**/short-links/2/stats", (route) =>
    route.fulfill({
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: {
          id: 2,
          originalUrl: "https://facebook.com/campaign",
          shortUrl: "https://sho.rt/facebook",
          clicks: 24521,
          uniqueClicks: 18392,
          topCountry: { code: "VN", clicks: 10231 },
          topReferrer: "facebook.com",
          recentActivities: [],
        },
      }),
    }),
  );

  await page.goto("/links/2");
  await expect(
    page.getByRole("heading", { name: "Facebook Campaign" }),
  ).toBeVisible();
  await expect(page.getByText("24.521", { exact: true })).toBeVisible();
  await expect(page.getByText("18.392", { exact: true })).toBeVisible();
  await expect(page.getByLabel("Biểu đồ click theo thời gian")).toBeVisible();
  await page.getByRole("button", { name: "7D" }).click();
});
