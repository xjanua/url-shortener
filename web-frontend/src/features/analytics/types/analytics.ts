export type DashboardSummary = {
  totalShortLinks: number;
  shortLinksCreatedToday: number;
  totalClicks: number;
  clicksToday: number;
};

export type DailyClick = {
  day: string;
  count: number;
};
