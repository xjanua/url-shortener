"use client";

import {
  ArrowSquareOutIcon,
  CheckIcon,
  CopyIcon,
  LinkSimpleHorizontalIcon,
} from "@phosphor-icons/react";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import type { ShortLink } from "../types/link";

type RecentLinksProps = {
  links: ShortLink[];
  loading: boolean;
  error: string | null;
  onRetry: () => void;
};

function displayName(link: ShortLink) {
  if (link.title?.trim()) return link.title;
  try {
    return new URL(link.originalUrl).hostname.replace(/^www\./, "");
  } catch {
    return "Liên kết không tiêu đề";
  }
}

function formatCreatedAt(value: string) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "Vừa tạo";
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }).format(date);
}

export function RecentLinks({
  links,
  loading,
  error,
  onRetry,
}: RecentLinksProps) {
  const [copiedId, setCopiedId] = useState<number | null>(null);

  async function copyLink(link: ShortLink) {
    await navigator.clipboard.writeText(link.shortUrl);
    setCopiedId(link.id);
    window.setTimeout(() => setCopiedId(null), 1800);
  }

  return (
    <section className="mt-8 w-full">
      <div className="mb-4 flex items-end justify-between gap-4">
        <div>
          <h2 className="text-lg font-semibold tracking-tight">
            Liên kết gần đây
          </h2>
          <p className="text-muted-foreground mt-1 text-sm">
            Năm liên kết được tạo gần nhất của bạn.
          </p>
        </div>
      </div>

      <div className="border-border bg-card overflow-hidden rounded-2xl border">
        {loading && (
          <div
            className="divide-border divide-y"
            aria-label="Đang tải liên kết"
          >
            {[0, 1, 2].map((item) => (
              <div key={item} className="flex items-center gap-4 p-4">
                <div className="bg-muted size-9 animate-pulse rounded-lg" />
                <div className="flex-1 space-y-2">
                  <div className="bg-muted h-4 w-36 animate-pulse rounded" />
                  <div className="bg-muted h-3 w-52 max-w-full animate-pulse rounded" />
                </div>
              </div>
            ))}
          </div>
        )}

        {!loading && error && (
          <div className="px-5 py-10 text-center">
            <p className="text-sm font-medium">Không thể tải liên kết</p>
            <p className="text-muted-foreground mt-1 text-sm">{error}</p>
            <Button
              type="button"
              variant="outline"
              className="mt-4"
              onClick={onRetry}
            >
              Thử lại
            </Button>
          </div>
        )}

        {!loading && !error && links.length === 0 && (
          <div className="px-5 py-12 text-center">
            <span className="bg-accent text-accent-foreground mx-auto grid size-11 place-items-center rounded-xl">
              <LinkSimpleHorizontalIcon className="size-5" />
            </span>
            <p className="mt-4 text-sm font-semibold">Chưa có liên kết nào</p>
            <p className="text-muted-foreground mx-auto mt-1 max-w-sm text-sm leading-6">
              Liên kết đầu tiên bạn tạo sẽ xuất hiện tại đây cùng số lượt nhấp.
            </p>
          </div>
        )}

        {!loading && !error && links.length > 0 && (
          <div className="divide-border divide-y">
            {links.map((link) => (
              <article
                key={link.id}
                className="hover:bg-muted/35 grid gap-4 px-4 py-4 transition-colors sm:grid-cols-[minmax(0,1fr)_auto] sm:items-center"
              >
                <div className="flex min-w-0 items-start gap-3.5">
                  <span className="bg-accent text-accent-foreground mt-0.5 grid size-9 shrink-0 place-items-center rounded-lg">
                    <LinkSimpleHorizontalIcon
                      className="size-4"
                      weight="bold"
                    />
                  </span>
                  <div className="min-w-0">
                    <h3 className="truncate text-sm font-semibold">
                      {displayName(link)}
                    </h3>
                    <p className="text-primary mt-1 truncate font-mono text-sm">
                      {link.shortUrl}
                    </p>
                    <p className="text-muted-foreground mt-1 truncate text-xs">
                      {link.originalUrl}
                    </p>
                  </div>
                </div>
                <div className="flex items-center justify-between gap-2 pl-[3.125rem] sm:justify-end sm:pl-0">
                  <div className="mr-2 text-right">
                    <p className="text-sm font-semibold tabular-nums">
                      {link.clickCount ?? 0}
                    </p>
                    <p className="text-muted-foreground text-xs">
                      {formatCreatedAt(link.createdAt)}
                    </p>
                  </div>
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon-sm"
                    aria-label={`Sao chép ${link.shortUrl}`}
                    title="Sao chép"
                    onClick={() => copyLink(link)}
                  >
                    {copiedId === link.id ? <CheckIcon /> : <CopyIcon />}
                  </Button>
                  <Button asChild variant="ghost" size="icon-sm">
                    <a
                      href={link.shortUrl}
                      target="_blank"
                      rel="noreferrer"
                      aria-label={`Mở ${link.shortUrl}`}
                      title="Mở liên kết"
                    >
                      <ArrowSquareOutIcon />
                    </a>
                  </Button>
                </div>
              </article>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
