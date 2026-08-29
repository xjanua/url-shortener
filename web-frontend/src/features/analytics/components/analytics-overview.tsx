"use client";

import {
  ArrowClockwiseIcon,
  ChartLineUpIcon,
  DesktopIcon,
  GlobeHemisphereWestIcon,
} from "@phosphor-icons/react";
import { useCallback, useEffect, useState } from "react";
import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

import { Button } from "@/components/ui/button";
import { getLinks } from "@/features/links/api/link-client";
import type { ShortLink } from "@/features/links/types/link";
import { getDailyClicks, getDashboardSummary } from "../api/analytics-client";
import type { DailyClick, DashboardSummary } from "../types/analytics";

const number = new Intl.NumberFormat("vi-VN");

function localDate(value: Date) {
  const year = value.getFullYear();
  const month = String(value.getMonth() + 1).padStart(2, "0");
  const day = String(value.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function chartRange() {
  const to = new Date();
  const from = new Date(to);
  from.setMonth(from.getMonth() - 11, 1);
  return { from: localDate(from), to: localDate(to) };
}

type MonthlyPoint = {
  month: string;
  label: string;
  count: number;
};

function monthKey(value: Date) {
  return `${value.getFullYear()}-${String(value.getMonth() + 1).padStart(2, "0")}`;
}

function createMonthlyPoints() {
  const current = new Date();
  return Array.from({ length: 12 }, (_, index) => {
    const date = new Date(
      current.getFullYear(),
      current.getMonth() - 11 + index,
      1,
    );
    return {
      month: monthKey(date),
      label: new Intl.DateTimeFormat("vi-VN", { month: "short" }).format(date),
      count: 0,
    };
  });
}

function groupDailyClicks(dailyClicks: DailyClick[]) {
  const points = createMonthlyPoints();
  const byMonth = new Map(points.map((point) => [point.month, point]));
  dailyClicks.forEach(({ day, count }) => {
    const point = byMonth.get(day.slice(0, 7));
    if (point) point.count += count;
  });
  return points;
}

function groupCreatedLinks(links: ShortLink[]) {
  const points = createMonthlyPoints();
  const byMonth = new Map(points.map((point) => [point.month, point]));
  links.forEach((link) => {
    const point = byMonth.get(link.createdAt.slice(0, 7));
    if (point) point.count += 1;
  });
  return points;
}

function YearChart({
  title,
  description,
  data,
  label,
  gradientId,
}: {
  title: string;
  description: string;
  data: MonthlyPoint[];
  label: string;
  gradientId: string;
}) {
  return (
    <section className="border-border bg-card rounded-xl border p-5 sm:p-6">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h2 className="font-semibold">{title}</h2>
          <p className="text-muted-foreground mt-1 text-sm">{description}</p>
        </div>
        <span className="bg-accent text-accent-foreground grid size-9 place-items-center rounded-lg">
          <ChartLineUpIcon className="size-[1.125rem]" weight="bold" />
        </span>
      </div>
      <div className="mt-7 h-72 w-full" aria-label={label}>
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart
            data={data}
            margin={{ top: 8, right: 8, left: -20, bottom: 0 }}
          >
            <defs>
              <linearGradient id={gradientId} x1="0" y1="0" x2="0" y2="1">
                <stop
                  offset="0%"
                  stopColor="var(--primary)"
                  stopOpacity={0.28}
                />
                <stop
                  offset="100%"
                  stopColor="var(--primary)"
                  stopOpacity={0}
                />
              </linearGradient>
            </defs>
            <CartesianGrid
              stroke="var(--border)"
              strokeDasharray="4 4"
              vertical={false}
            />
            <XAxis
              dataKey="label"
              axisLine={false}
              tickLine={false}
              tick={{ fill: "var(--muted-foreground)", fontSize: 12 }}
              dy={8}
            />
            <YAxis
              allowDecimals={false}
              axisLine={false}
              tickLine={false}
              tick={{ fill: "var(--muted-foreground)", fontSize: 12 }}
            />
            <Tooltip
              formatter={(value) => [number.format(Number(value)), title]}
              contentStyle={{
                background: "var(--popover)",
                border: "1px solid var(--border)",
                borderRadius: 10,
                color: "var(--popover-foreground)",
                fontSize: 13,
              }}
            />
            <Area
              type="monotone"
              dataKey="count"
              stroke="var(--primary)"
              strokeWidth={2}
              fill={`url(#${gradientId})`}
              activeDot={{ r: 4 }}
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </section>
  );
}

export function AnalyticsOverview() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [dailyClicks, setDailyClicks] = useState<DailyClick[]>([]);
  const [links, setLinks] = useState<ShortLink[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeBreakdown, setActiveBreakdown] = useState("Locations");

  const loadAnalytics = useCallback(async () => {
    setLoading(true);
    setError(null);
    const range = chartRange();
    const [summaryResponse, dailyResponse, linksResponse] = await Promise.all([
      getDashboardSummary(),
      getDailyClicks(range.from, range.to),
      getLinks(1000),
    ]);

    if (!summaryResponse.success) {
      setError(summaryResponse.error.message);
    } else if (!dailyResponse.success) {
      setError(dailyResponse.error.message);
    } else if (!linksResponse.success) {
      setError(linksResponse.error.message);
    } else {
      setSummary(summaryResponse.data);
      setDailyClicks(dailyResponse.data);
      setLinks(linksResponse.data.response);
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    let cancelled = false;
    const range = chartRange();
    void Promise.all([
      getDashboardSummary(),
      getDailyClicks(range.from, range.to),
      getLinks(1000),
    ]).then(([summaryResponse, dailyResponse, linksResponse]) => {
      if (cancelled) return;
      if (!summaryResponse.success) {
        setError(summaryResponse.error.message);
      } else if (!dailyResponse.success) {
        setError(dailyResponse.error.message);
      } else if (!linksResponse.success) {
        setError(linksResponse.error.message);
      } else {
        setSummary(summaryResponse.data);
        setDailyClicks(dailyResponse.data);
        setLinks(linksResponse.data.response);
      }
      setLoading(false);
    });
    return () => {
      cancelled = true;
    };
  }, []);

  const metrics = summary
    ? [
        ["Tổng liên kết", summary.totalShortLinks],
        ["Tạo hôm nay", summary.shortLinksCreatedToday],
        ["Tổng lượt nhấp", summary.totalClicks],
        ["Nhấp hôm nay", summary.clicksToday],
      ]
    : [];
  const monthlyClicks = groupDailyClicks(dailyClicks);
  const monthlyLinks = groupCreatedLinks(links);

  const breakdowns: Record<string, Array<[string, string]>> = {
    Locations: [
      ["Vietnam", "42,8%"],
      ["United States", "18,4%"],
      ["Singapore", "8,2%"],
      ["Japan", "5,1%"],
    ],
    Devices: [
      ["Desktop", "58%"],
      ["Mobile", "36%"],
      ["Tablet", "6%"],
    ],
    Browsers: [
      ["Chrome", "64%"],
      ["Safari", "22%"],
      ["Edge", "9%"],
      ["Firefox", "5%"],
    ],
    "Operating Systems": [
      ["Windows", "47%"],
      ["iOS", "25%"],
      ["Android", "20%"],
      ["macOS", "8%"],
    ],
    Referrers: [
      ["Direct", "39%"],
      ["google.com", "24%"],
      ["facebook.com", "18%"],
      ["linkedin.com", "7%"],
    ],
    "Traffic Sources": [
      ["Direct", "39%"],
      ["Search", "24%"],
      ["Social", "22%"],
      ["Referral", "15%"],
    ],
  };
  const tabs = [
    "Overview",
    "Locations",
    "Devices",
    "Browsers",
    "Operating Systems",
    "Referrers",
    "Traffic Sources",
  ];

  return (
    <main className="min-h-[100dvh] px-4 py-10 sm:px-6 md:px-10 md:py-14">
      <div className="mx-auto max-w-6xl">
        <div className="flex items-start justify-between gap-5">
          <div>
            <p className="text-primary text-sm font-semibold">Tổng quan</p>
            <h1 className="mt-2 text-3xl font-semibold tracking-[-0.04em]">
              Phân tích
            </h1>
            <p className="text-muted-foreground mt-2 text-sm leading-6">
              Theo dõi link tạo mới và lượt nhấp theo từng tháng trong 12 tháng
              gần nhất.
            </p>
          </div>
          <Button
            type="button"
            variant="outline"
            disabled={loading}
            onClick={loadAnalytics}
          >
            <ArrowClockwiseIcon className={loading ? "animate-spin" : ""} />
            Làm mới
          </Button>
        </div>

        {loading && (
          <div className="border-border bg-border mt-10 grid animate-pulse gap-px overflow-hidden rounded-xl border sm:grid-cols-2 lg:grid-cols-4">
            {[0, 1, 2, 3].map((item) => (
              <div key={item} className="bg-card h-28 p-5">
                <div className="bg-muted h-3 w-24 rounded" />
                <div className="bg-muted mt-4 h-7 w-16 rounded" />
              </div>
            ))}
          </div>
        )}

        {!loading && error && (
          <div className="border-border bg-card mt-10 rounded-xl border px-5 py-12 text-center">
            <p className="font-semibold">Không thể tải dữ liệu phân tích</p>
            <p className="text-muted-foreground mt-1 text-sm">{error}</p>
            <Button
              type="button"
              variant="outline"
              className="mt-4"
              onClick={loadAnalytics}
            >
              Thử lại
            </Button>
          </div>
        )}

        {!loading && !error && summary && (
          <>
            <section
              className="border-border bg-border mt-10 grid gap-px overflow-hidden rounded-xl border sm:grid-cols-2 lg:grid-cols-4"
              aria-label="Chỉ số tổng quan"
            >
              {metrics.map(([label, value]) => (
                <div key={label} className="bg-card p-5 sm:p-6">
                  <p className="text-muted-foreground text-sm">{label}</p>
                  <p className="mt-3 text-2xl font-semibold tracking-tight tabular-nums">
                    {number.format(Number(value))}
                  </p>
                </div>
              ))}
            </section>

            <div className="mt-6 grid gap-6">
              <YearChart
                title="Liên kết tạo mới"
                description="Số short link được tạo theo tháng."
                data={monthlyLinks}
                label="Biểu đồ liên kết tạo mới trong 12 tháng"
                gradientId="links-created-fill"
              />
              <YearChart
                title="Lượt nhấp"
                description="Tổng lượt nhấp theo tháng."
                data={monthlyClicks}
                label="Biểu đồ lượt nhấp trong 12 tháng"
                gradientId="clicks-year-fill"
              />
            </div>

            <section className="border-border bg-card mt-6 rounded-xl border p-5 sm:p-6">
              <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <h2 className="font-semibold">Audience insights</h2>
                  <p className="text-muted-foreground mt-1 text-sm">
                    Hiểu người truy cập theo vị trí, thiết bị và nguồn traffic.
                  </p>
                </div>
                <span className="text-muted-foreground text-xs">
                  Bản xem trước dữ liệu
                </span>
              </div>
              <div
                className="border-border mt-6 flex gap-1 overflow-x-auto border-b pb-3"
                role="tablist"
                aria-label="Nhóm phân tích"
              >
                {tabs.map((tab) => (
                  <Button
                    key={tab}
                    type="button"
                    size="sm"
                    variant={
                      tab === "Overview"
                        ? activeBreakdown === "Locations"
                          ? "secondary"
                          : "ghost"
                        : activeBreakdown === tab
                          ? "secondary"
                          : "ghost"
                    }
                    className="shrink-0"
                    role="tab"
                    aria-selected={
                      tab === "Overview"
                        ? activeBreakdown === "Locations"
                        : activeBreakdown === tab
                    }
                    onClick={() =>
                      setActiveBreakdown(tab === "Overview" ? "Locations" : tab)
                    }
                  >
                    {tab}
                  </Button>
                ))}
              </div>
              <div className="mt-6 grid gap-6 lg:grid-cols-2">
                <div>
                  <div className="flex items-center gap-2">
                    {activeBreakdown === "Locations" ? (
                      <GlobeHemisphereWestIcon className="text-primary size-5" />
                    ) : (
                      <DesktopIcon className="text-primary size-5" />
                    )}
                    <h3 className="text-sm font-semibold">
                      {activeBreakdown === "Locations"
                        ? "Top countries"
                        : activeBreakdown}
                    </h3>
                  </div>
                  <div className="mt-5 space-y-4">
                    {(breakdowns[activeBreakdown] ?? []).map(
                      ([label, value], index) => (
                        <div
                          key={label}
                          className="grid grid-cols-[minmax(0,1fr)_auto] items-center gap-4"
                        >
                          <div className="min-w-0">
                            <div className="flex items-center justify-between gap-3 text-sm">
                              <span className="truncate font-medium">
                                {label}
                              </span>
                              <span className="text-muted-foreground tabular-nums lg:hidden">
                                {value}
                              </span>
                            </div>
                            <div className="bg-muted mt-2 h-1.5 overflow-hidden rounded-full">
                              <div
                                className="bg-primary h-full rounded-full"
                                style={{
                                  width: `${Math.max(18, 100 - index * 21)}%`,
                                }}
                              />
                            </div>
                          </div>
                          <span className="text-muted-foreground hidden text-sm tabular-nums lg:block">
                            {value}
                          </span>
                        </div>
                      ),
                    )}
                  </div>
                </div>
                <div className="border-border bg-muted/35 rounded-xl border p-5">
                  <p className="text-sm font-semibold">Sự kiện gần nhất</p>
                  <p className="text-muted-foreground mt-1 text-sm leading-6">
                    Khi click events được mở rộng, khu vực này sẽ cho phép lọc
                    theo city, language, referrer, bot và timestamp.
                  </p>
                  <Button
                    type="button"
                    variant="outline"
                    className="mt-5"
                    disabled
                  >
                    Xem click events
                  </Button>
                </div>
              </div>
            </section>
          </>
        )}
      </div>
    </main>
  );
}
