import { expect, test } from "@playwright/test";

test("shows monthly link and click charts for the last 12 months", async ({
  page,
}) => {
  const now = new Date();
  const dailyClicks = Array.from({ length: 12 }, (_, index) => {
    const date = new Date(now.getFullYear(), now.getMonth() - 11 + index, 8);
    return {
      day: `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-08`,
      count: (index + 1) * 100,
    };
  });

  await page.route("**/dashboard/summary", (route) =>
    route.fulfill({
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: {
          totalShortLinks: 18,
          shortLinksCreatedToday: 1,
          totalClicks: 7800,
          clicksToday: 120,
        },
      }),
    }),
  );
  await page.route("**/dashboard/daily-clicks?*", (route) =>
    route.fulfill({
      contentType: "application/json",
      body: JSON.stringify({ success: true, error: null, data: dailyClicks }),
    }),
  );
  await page.route("**/short-links?*", (route) =>
    route.fulfill({
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        error: null,
        data: {
          info: { page: 0, size: 1000, pages: 1, total: 2 },
          response: [
            {
              id: 1,
              title: "One",
              originalUrl: "https://example.com/one",
              shortUrl: "https://sho.rt/one",
              status: "ACTIVE",
              clickCount: 10,
              uniqueClicks: 8,
              createdAt: dailyClicks[0]!.day + "T09:00:00",
            },
            {
              id: 2,
              title: "Two",
              originalUrl: "https://example.com/two",
              shortUrl: "https://sho.rt/two",
              status: "ACTIVE",
              clickCount: 20,
              uniqueClicks: 14,
              createdAt: dailyClicks[11]!.day + "T09:00:00",
            },
          ],
        },
      }),
    }),
  );

  await page.goto("/analytics");
  await expect(
    page.getByLabel("Biểu đồ liên kết tạo mới trong 12 tháng"),
  ).toBeVisible();
  await expect(
    page.getByLabel("Biểu đồ lượt nhấp trong 12 tháng"),
  ).toBeVisible();
});
