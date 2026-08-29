"use client";

import {
  ArrowRightIcon,
  ChartLineUpIcon,
  CursorClickIcon,
  HandWavingIcon,
  LinkSimpleHorizontalIcon,
  PlusIcon,
  SparkleIcon,
} from "@phosphor-icons/react";
import Link from "next/link";
import { useEffect, useState } from "react";
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
import { getDashboardData } from "../api/dashboard-client";
import type {
  DailyClick,
  DashboardLink,
  DashboardSummary,
} from "../types/dashboard";

const number = new Intl.NumberFormat("vi-VN");

function localDate(value: Date) {
  const year = value.getFullYear();
  const month = String(value.getMonth() + 1).padStart(2, "0");
  const day = String(value.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function range() {
  const to = new Date();
  const from = new Date(to);
  from.setDate(from.getDate() - 6);
  return { from: localDate(from), to: localDate(to) };
}

function formatDay(value: string) {
  const [, month, day] = value.split("-");
  return `${day}/${month}`;
}

function displayName(link: DashboardLink) {
  if (link.title?.trim()) return link.title;
  try {
    return new URL(link.originalUrl).hostname.replace(/^www\./, "");
  } catch {
    return "Liên kết không tiêu đề";
  }
}

export function DashboardOverview() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [dailyClicks, setDailyClicks] = useState<DailyClick[]>([]);
  const [links, setLinks] = useState<DashboardLink[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    const dates = range();
    void getDashboardData(dates.from, dates.to).then(
      ([summaryResult, clicksResult, linksResult]) => {
        if (cancelled) return;
        const failed = [summaryResult, clicksResult, linksResult].find(
          (result) => !result.success,
        );
        if (failed && !failed.success) {
          setError(failed.error.message);
        } else if (
          summaryResult.success &&
          clicksResult.success &&
          linksResult.success
        ) {
          setSummary(summaryResult.data);
          setDailyClicks(clicksResult.data);
          setLinks(linksResult.data.response);
        }
        setLoading(false);
      },
    );
    return () => {
      cancelled = true;
    };
  }, []);

  const metrics = summary
    ? [
        {
          label: "Tổng liên kết",
          value: summary.totalShortLinks,
          note: `${number.format(summary.shortLinksCreatedToday)} tạo hôm nay`,
          icon: LinkSimpleHorizontalIcon,
        },
        {
          label: "Tổng lượt nhấp",
          value: summary.totalClicks,
          note: `${number.format(summary.clicksToday)} lượt hôm nay`,
          icon: CursorClickIcon,
        },
        {
          label: "Liên kết hôm nay",
          value: summary.shortLinksCreatedToday,
          note: "Tính từ 00:00",
          icon: SparkleIcon,
        },
        {
          label: "Nhấp hôm nay",
          value: summary.clicksToday,
          note: "Cập nhật theo thời gian",
          icon: ChartLineUpIcon,
        },
      ]
    : [];

  return (
    <main className="min-h-[100dvh] px-4 py-8 sm:px-6 md:px-8 lg:px-10">
      <div className="mx-auto max-w-7xl">
        <div className="flex flex-col justify-between gap-5 sm:flex-row sm:items-end">
          <div>
            <h1 className="flex items-center gap-2 text-2xl font-semibold tracking-[-0.035em] sm:text-3xl">
              Chào buổi chiều, Toàn{" "}
              <HandWavingIcon className="text-primary size-6" weight="fill" />
            </h1>
            <p className="text-muted-foreground mt-2 text-sm">
              Đây là tình hình hoạt động của các liên kết hôm nay.
            </p>
          </div>
          <Button asChild size="lg" className="w-full sm:w-auto">
            <Link href="/links">
              <PlusIcon weight="bold" />
              Tạo liên kết ngắn
            </Link>
          </Button>
        </div>

        {loading && <DashboardSkeleton />}

        {!loading && error && (
          <div className="border-border bg-card mt-8 rounded-xl border px-5 py-12 text-center">
            <p className="font-semibold">Không thể tải dashboard</p>
            <p className="text-muted-foreground mt-1 text-sm">{error}</p>
            <Button
              type="button"
              variant="outline"
              className="mt-4"
              onClick={() => window.location.reload()}
            >
              Thử lại
            </Button>
          </div>
        )}

        {!loading && !error && summary && (
          <>
            <section
              className="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4"
              aria-label="Chỉ số tổng quan"
            >
              {metrics.map(({ label, value, note, icon: Icon }) => (
                <article
                  key={label}
                  className="border-border bg-card rounded-xl border p-5 shadow-sm"
                >
                  <div className="flex items-start justify-between gap-4">
                    <p className="text-muted-foreground text-sm font-medium">
                      {label}
                    </p>
                    <span className="bg-accent text-accent-foreground grid size-9 place-items-center rounded-lg">
                      <Icon className="size-[1.125rem]" weight="bold" />
                    </span>
                  </div>
                  <p className="mt-5 text-3xl font-semibold tracking-[-0.04em] tabular-nums">
                    {number.format(value)}
                  </p>
                  <p className="text-muted-foreground mt-1 text-xs">{note}</p>
                </article>
              ))}
            </section>

            <section className="border-border bg-card mt-6 rounded-xl border p-5 shadow-sm sm:p-6">
              <div>
                <h2 className="font-semibold">Hoạt động lượt nhấp</h2>
                <p className="text-muted-foreground mt-1 text-sm">
                  7 ngày gần nhất
                </p>
              </div>
              {dailyClicks.length === 0 ? (
                <div className="grid h-72 place-items-center text-center">
                  <div>
                    <p className="text-sm font-semibold">Chưa có lượt nhấp</p>
                    <p className="text-muted-foreground mt-1 text-sm">
                      Dữ liệu sẽ xuất hiện khi liên kết được truy cập.
                    </p>
                  </div>
                </div>
              ) : (
                <div
                  className="mt-6 h-72 w-full"
                  aria-label="Biểu đồ lượt nhấp trong 7 ngày"
                >
                  <ResponsiveContainer width="100%" height="100%">
                    <AreaChart
                      data={dailyClicks}
                      margin={{ top: 8, right: 8, left: -20, bottom: 0 }}
                    >
                      <defs>
                        <linearGradient
                          id="dashboard-click-fill"
                          x1="0"
                          y1="0"
                          x2="0"
                          y2="1"
                        >
                          <stop
                            offset="0%"
                            stopColor="var(--primary)"
                            stopOpacity={0.24}
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
                        dataKey="day"
                        tickFormatter={formatDay}
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
                        labelFormatter={(label) =>
                          `Ngày ${formatDay(String(label))}`
                        }
                        formatter={(value) => [
                          number.format(Number(value)),
                          "Lượt nhấp",
                        ]}
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
                        fill="url(#dashboard-click-fill)"
                        activeDot={{ r: 4 }}
                      />
                    </AreaChart>
                  </ResponsiveContainer>
                </div>
              )}
            </section>

            <section className="border-border bg-card mt-6 overflow-hidden rounded-xl border shadow-sm">
              <div className="border-border flex items-center justify-between border-b px-5 py-4">
                <div>
                  <h2 className="font-semibold">Liên kết gần đây</h2>
                  <p className="text-muted-foreground mt-1 text-sm">
                    Ba liên kết mới nhất
                  </p>
                </div>
                <Button asChild variant="ghost">
                  <Link href="/links">
                    Xem tất cả <ArrowRightIcon />
                  </Link>
                </Button>
              </div>
              {links.length === 0 ? (
                <div className="text-muted-foreground px-5 py-10 text-center text-sm">
                  Chưa có liên kết nào.
                </div>
              ) : (
                <div className="divide-border divide-y">
                  {links.map((link) => (
                    <article
                      key={link.id}
                      className="grid gap-3 px-5 py-4 sm:grid-cols-[minmax(0,1fr)_auto] sm:items-center"
                    >
                      <div className="min-w-0">
                        <p className="truncate text-sm font-semibold">
                          {displayName(link)}
                        </p>
                        <p className="text-primary mt-1 truncate font-mono text-sm">
                          {link.shortUrl}
                        </p>
                      </div>
                      <div className="text-left sm:text-right">
                        <p className="text-sm font-semibold tabular-nums">
                          {number.format(link.clickCount ?? 0)}
                        </p>
                        <p className="text-muted-foreground text-xs">
                          lượt nhấp
                        </p>
                      </div>
                    </article>
                  ))}
                </div>
              )}
            </section>
          </>
        )}
      </div>
    </main>
  );
}

function DashboardSkeleton() {
  return (
    <div className="mt-8 animate-pulse">
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {[0, 1, 2, 3].map((item) => (
          <div
            key={item}
            className="border-border bg-card h-36 rounded-xl border p-5"
          >
            <div className="bg-muted h-3 w-24 rounded" />
            <div className="bg-muted mt-7 h-8 w-20 rounded" />
          </div>
        ))}
      </div>
      <div className="border-border bg-card mt-6 h-80 rounded-xl border" />
      <div className="border-border bg-card mt-6 h-52 rounded-xl border" />
    </div>
  );
}
