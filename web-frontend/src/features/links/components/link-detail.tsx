"use client";

import {
  ArrowLeftIcon,
  CheckIcon,
  CopyIcon,
  PencilSimpleIcon,
  QrCodeIcon,
} from "@phosphor-icons/react";
import Link from "next/link";
import { useCallback, useEffect, useMemo, useState } from "react";
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
import { getLinkStats, getLinks } from "../api/link-client";
import type { LinkStats, ShortLink } from "../types/link";

const number = new Intl.NumberFormat("vi-VN");
const ranges = ["24h", "7D", "30D", "90D"] as const;

function dates(days: number, clicks: number) {
  const today = new Date();
  return Array.from({ length: days }, (_, index) => {
    const date = new Date(today);
    date.setDate(today.getDate() - (days - index - 1));
    const volatility = 0.54 + ((index * 17 + days) % 29) / 36;
    return {
      day: `${date.getDate()}/${date.getMonth() + 1}`,
      clicks: Math.max(0, Math.round((clicks / days) * volatility)),
    };
  });
}

function created(value?: string) {
  if (!value) return "Chưa xác định";
  const date = new Date(value);
  return Number.isNaN(date.getTime())
    ? "Chưa xác định"
    : new Intl.DateTimeFormat("vi-VN", {
        day: "numeric",
        month: "long",
        year: "numeric",
      }).format(date);
}

export function LinkDetail({ id }: { id: number }) {
  const [link, setLink] = useState<ShortLink | null>(null);
  const [stats, setStats] = useState<LinkStats | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [copied, setCopied] = useState(false);
  const [range, setRange] = useState<(typeof ranges)[number]>("30D");

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    const [linksResponse, statsResponse] = await Promise.all([
      getLinks(100),
      getLinkStats(id),
    ]);
    if (!statsResponse.success) {
      setError(statsResponse.error.message);
    } else {
      setStats(statsResponse.data);
      if (linksResponse.success) {
        setLink(
          linksResponse.data.response.find((item) => item.id === id) ?? null,
        );
      }
    }
    setLoading(false);
  }, [id]);

  useEffect(() => {
    let cancelled = false;
    void Promise.all([getLinks(100), getLinkStats(id)]).then(
      ([linksResponse, statsResponse]) => {
        if (cancelled) return;
        if (!statsResponse.success) {
          setError(statsResponse.error.message);
        } else {
          setStats(statsResponse.data);
          if (linksResponse.success) {
            setLink(
              linksResponse.data.response.find((item) => item.id === id) ??
                null,
            );
          }
        }
        setLoading(false);
      },
    );
    return () => {
      cancelled = true;
    };
  }, [id]);

  const days =
    range === "24h" ? 1 : range === "7D" ? 7 : range === "30D" ? 30 : 90;
  const chartData = useMemo(
    () => dates(days, stats?.clicks ?? 0),
    [days, stats?.clicks],
  );
  const uniqueRate = stats?.clicks
    ? Math.round((stats.uniqueClicks / stats.clicks) * 1000) / 10
    : 0;

  async function copy() {
    if (!stats) return;
    await navigator.clipboard.writeText(stats.shortUrl);
    setCopied(true);
    window.setTimeout(() => setCopied(false), 1800);
  }

  if (loading) {
    return (
      <main className="mx-auto min-h-[100dvh] max-w-[1400px] px-4 py-8 sm:px-6 md:px-8 lg:px-10">
        <div className="bg-muted h-8 w-48 animate-pulse rounded" />
        <div className="bg-muted mt-7 h-48 animate-pulse rounded-xl" />
        <div className="bg-muted mt-6 h-96 animate-pulse rounded-xl" />
      </main>
    );
  }

  if (error || !stats) {
    return (
      <main className="mx-auto grid min-h-[100dvh] max-w-[1400px] place-items-center px-4">
        <div className="text-center">
          <p className="font-semibold">Không thể tải chi tiết liên kết</p>
          <p className="text-muted-foreground mt-1 text-sm">
            {error ?? "Liên kết không tồn tại."}
          </p>
          <Button
            type="button"
            variant="outline"
            className="mt-4"
            onClick={load}
          >
            Thử lại
          </Button>
        </div>
      </main>
    );
  }

  const title =
    link?.title?.trim() || stats.shortUrl.replace(/^https?:\/\//, "");
  const metricItems = [
    ["Clicks", number.format(stats.clicks)],
    ["Khách truy cập duy nhất", number.format(stats.uniqueClicks)],
    ["Tỷ lệ duy nhất", `${uniqueRate}%`],
    ["Quốc gia hàng đầu", stats.topCountry?.code ?? "Chưa có"],
  ];

  return (
    <main className="mx-auto min-h-[100dvh] max-w-[1400px] px-4 py-8 sm:px-6 md:px-8 md:py-10 lg:px-10">
      <Link
        href="/links"
        className="text-muted-foreground hover:text-foreground inline-flex items-center gap-2 text-sm font-medium"
      >
        <ArrowLeftIcon /> Tất cả links
      </Link>
      <header className="border-border mt-6 border-b pb-7">
        <div className="flex flex-col justify-between gap-5 lg:flex-row lg:items-start">
          <div className="min-w-0">
            <h1 className="truncate text-3xl font-semibold tracking-[-0.04em]">
              {title}
            </h1>
            <p className="text-primary mt-3 truncate font-mono text-base font-semibold">
              {stats.shortUrl}
            </p>
            <p className="text-muted-foreground mt-2 truncate text-sm">
              {stats.originalUrl}
            </p>
            <p className="text-muted-foreground mt-3 text-sm">
              Tạo ngày {created(link?.createdAt)}
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            <Button type="button" variant="outline" onClick={copy}>
              {copied ? <CheckIcon /> : <CopyIcon />}
              {copied ? "Đã sao chép" : "Sao chép"}
            </Button>
            <Button type="button" variant="outline">
              <QrCodeIcon /> QR
            </Button>
            <Button type="button">
              <PencilSimpleIcon /> Chỉnh sửa
            </Button>
          </div>
        </div>
      </header>

      <section
        className="border-border bg-border mt-6 grid gap-px overflow-hidden rounded-xl border sm:grid-cols-2 xl:grid-cols-4"
        aria-label="Chỉ số liên kết"
      >
        {metricItems.map(([label, value]) => (
          <div key={label} className="bg-card p-5">
            <p className="text-muted-foreground text-sm">{label}</p>
            <p className="mt-3 text-2xl font-semibold tracking-tight tabular-nums">
              {value}
            </p>
          </div>
        ))}
      </section>

      <section className="border-border bg-card mt-6 rounded-xl border p-5 sm:p-6">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <h2 className="font-semibold">Clicks</h2>
            <p className="text-muted-foreground mt-1 text-sm">
              Diễn biến lượt truy cập theo thời gian.
            </p>
          </div>
          <div
            className="flex flex-wrap gap-1"
            role="group"
            aria-label="Khoảng thời gian"
          >
            {ranges.map((item) => (
              <Button
                key={item}
                type="button"
                size="sm"
                variant={range === item ? "secondary" : "ghost"}
                onClick={() => setRange(item)}
              >
                {item}
              </Button>
            ))}
            <Button type="button" size="sm" variant="ghost">
              Tùy chỉnh
            </Button>
          </div>
        </div>
        <div className="mt-6 h-72" aria-label="Biểu đồ click theo thời gian">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart
              data={chartData}
              margin={{ top: 8, right: 4, left: -22, bottom: 0 }}
            >
              <defs>
                <linearGradient
                  id="link-clicks-fill"
                  x1="0"
                  y1="0"
                  x2="0"
                  y2="1"
                >
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
                dataKey="day"
                axisLine={false}
                tickLine={false}
                tick={{ fill: "var(--muted-foreground)", fontSize: 12 }}
                dy={8}
                interval={Math.max(0, Math.floor(days / 6) - 1)}
              />
              <YAxis
                axisLine={false}
                tickLine={false}
                tick={{ fill: "var(--muted-foreground)", fontSize: 12 }}
              />
              <Tooltip
                formatter={(value) => [number.format(Number(value)), "Clicks"]}
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
                dataKey="clicks"
                stroke="var(--primary)"
                strokeWidth={2}
                fill="url(#link-clicks-fill)"
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </section>
    </main>
  );
}
