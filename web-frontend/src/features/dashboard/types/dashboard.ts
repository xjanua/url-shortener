export type DashboardSummary = {
  totalShortLinks: number;
  shortLinksCreatedToday: number;
  totalClicks: number;
  clicksToday: number;
};

export type DailyClick = { day: string; count: number };

export type DashboardLink = {
  id: number;
  title: string | null;
  originalUrl: string;
  shortUrl: string;
  clickCount: number;
  createdAt: string;
};

export type DashboardLinksResponse = {
  info: { page: number; size: number; pages: number; total: number };
  response: DashboardLink[];
};
