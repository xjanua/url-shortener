export type ShortLinkStatus = "ACTIVE" | "ARCHIVED" | "DELETED";

export type ShortLink = {
  id: number;
  title: string | null;
  originalUrl: string;
  shortCode?: string;
  shortUrl: string;
  status: ShortLinkStatus;
  clickCount: number;
  uniqueClicks: number;
  createdAt: string;
  updatedAt?: string;
};

export type PaginationInfo = {
  page: number;
  size: number;
  pages: number;
  total: number;
};

export type PaginatedLinks = {
  info: PaginationInfo;
  response: ShortLink[];
};

export type LinkStats = {
  id: number;
  originalUrl: string;
  shortUrl: string;
  clicks: number;
  uniqueClicks: number;
  topCountry: { code: string; clicks: number } | null;
  topReferrer: string | null;
  recentActivities: Array<{
    countryCode: string | null;
    deviceType: string | null;
    operatingSystem: string | null;
    browser: string | null;
    referrer: string | null;
    clickedAt: string;
  }>;
};
