"use client";

import {
  ChartLineUpIcon,
  CheckIcon,
  CopyIcon,
  DotsThreeIcon,
  MagnifyingGlassIcon,
  PlusIcon,
  QrCodeIcon,
  SlidersHorizontalIcon,
} from "@phosphor-icons/react";
import Link from "next/link";
import { useCallback, useEffect, useMemo, useState } from "react";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import { getLinks, updateLinkStatus } from "../api/link-client";
import type { ShortLink, ShortLinkStatus } from "../types/link";
import { CreateLinkSpotlight } from "./create-link-spotlight";

const number = new Intl.NumberFormat("vi-VN");

function destination(url: string) {
  try {
    const parsed = new URL(url);
    return `${parsed.hostname.replace(/^www\./, "")}${
      parsed.pathname === "/" ? "" : parsed.pathname
    }`;
  } catch {
    return url;
  }
}

function statusLabel(status: ShortLinkStatus) {
  if (status === "ACTIVE") return "Đang hoạt động";
  if (status === "ARCHIVED") return "Đã tắt";
  return "Đã xóa";
}

function statusClass(status: ShortLinkStatus) {
  if (status === "ACTIVE") return "bg-success/12 text-success";
  if (status === "ARCHIVED") return "bg-muted text-muted-foreground";
  return "bg-destructive/10 text-destructive";
}

export function LinkWorkspace() {
  const [links, setLinks] = useState<ShortLink[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState<"ALL" | ShortLinkStatus>("ALL");
  const [menuId, setMenuId] = useState<number | null>(null);
  const [copiedId, setCopiedId] = useState<number | null>(null);
  const [createOpen, setCreateOpen] = useState(false);

  const loadLinks = useCallback(async () => {
    setLoading(true);
    setError(null);
    const response = await getLinks();
    if (response.success) setLinks(response.data.response);
    else setError(response.error.message);
    setLoading(false);
  }, []);

  useEffect(() => {
    let cancelled = false;
    void getLinks().then((response) => {
      if (cancelled) return;
      if (response.success) setLinks(response.data.response);
      else setError(response.error.message);
      setLoading(false);
    });
    return () => {
      cancelled = true;
    };
  }, [loadLinks]);

  const filteredLinks = useMemo(() => {
    const needle = query.trim().toLowerCase();
    return links.filter((link) => {
      const matchesStatus = status === "ALL" || link.status === status;
      const matchesQuery =
        !needle ||
        [link.title, link.shortUrl, link.originalUrl]
          .filter(Boolean)
          .some((value) => value?.toLowerCase().includes(needle));
      return matchesStatus && matchesQuery;
    });
  }, [links, query, status]);

  async function copyLink(link: ShortLink) {
    await navigator.clipboard.writeText(link.shortUrl);
    setCopiedId(link.id);
    window.setTimeout(() => setCopiedId(null), 1800);
  }

  async function changeStatus(link: ShortLink, nextStatus: ShortLinkStatus) {
    setMenuId(null);
    const response = await updateLinkStatus(link.id, nextStatus);
    if (response.success) {
      setLinks((current) =>
        current.map((item) => (item.id === link.id ? response.data : item)),
      );
    }
  }

  function addCreatedLink(createdLink: ShortLink) {
    setLinks((current) => [
      createdLink,
      ...current.filter((item) => item.id !== createdLink.id),
    ]);
    setCreateOpen(false);
  }

  return (
    <main className="mx-auto min-h-[100dvh] max-w-[1400px] px-4 py-8 sm:px-6 md:px-8 md:py-10 lg:px-10">
      <header className="flex flex-col gap-5 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-3xl font-semibold tracking-[-0.04em]">
            Short Links
          </h1>
          <p className="text-muted-foreground mt-1.5 text-sm">
            Tạo, quản lý và theo dõi mọi liên kết của bạn.
          </p>
        </div>
        <Button
          type="button"
          className="w-full sm:w-auto"
          onClick={() => setCreateOpen(true)}
        >
          <PlusIcon weight="bold" /> Tạo link
        </Button>
      </header>

      <section className="mt-8" aria-label="Danh sách short link">
        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div className="relative w-full md:max-w-md">
            <MagnifyingGlassIcon className="text-muted-foreground absolute top-1/2 left-3.5 size-4 -translate-y-1/2" />
            <Input
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              className="h-10 pl-10"
              placeholder="Tìm theo tên, short URL hoặc URL đích"
              aria-label="Tìm liên kết"
            />
          </div>
          <div className="grid grid-cols-2 gap-2 sm:flex">
            <label className="sr-only" htmlFor="link-status">
              Trạng thái
            </label>
            <select
              id="link-status"
              value={status}
              onChange={(event) =>
                setStatus(event.target.value as "ALL" | ShortLinkStatus)
              }
              className="border-border bg-card focus-visible:ring-ring/35 h-10 rounded-lg border px-3 text-sm font-medium outline-none focus-visible:ring-3"
            >
              <option value="ALL">Tất cả trạng thái</option>
              <option value="ACTIVE">Đang hoạt động</option>
              <option value="ARCHIVED">Đã tắt</option>
              <option value="DELETED">Đã xóa</option>
            </select>
            <Button type="button" variant="outline" className="h-10">
              <SlidersHorizontalIcon /> Mới tạo
            </Button>
          </div>
        </div>

        <div className="border-border bg-card mt-5 overflow-visible rounded-xl border">
          <div className="text-muted-foreground hidden grid-cols-[minmax(10rem,1.3fr)_minmax(12rem,1.5fr)_6rem_8rem_7rem] gap-5 border-b px-5 py-3 text-xs font-semibold tracking-wide uppercase lg:grid">
            <span>Short URL</span>
            <span>Đích đến</span>
            <span className="text-right">Clicks</span>
            <span>Trạng thái</span>
            <span className="text-right">Thao tác</span>
          </div>

          {loading &&
            [0, 1, 2, 3].map((item) => (
              <div
                key={item}
                className="border-border grid h-[5.35rem] animate-pulse border-b last:border-b-0"
              >
                <div className="bg-muted m-5 rounded" />
              </div>
            ))}

          {!loading && error && (
            <div className="px-5 py-14 text-center">
              <p className="font-semibold">Không thể tải liên kết</p>
              <p className="text-muted-foreground mt-1 text-sm">{error}</p>
              <Button
                type="button"
                variant="outline"
                className="mt-4"
                onClick={loadLinks}
              >
                Thử lại
              </Button>
            </div>
          )}

          {!loading && !error && filteredLinks.length === 0 && (
            <div className="px-5 py-14 text-center">
              <p className="font-semibold">Không tìm thấy liên kết</p>
              <p className="text-muted-foreground mt-1 text-sm">
                Thử thay đổi từ khóa tìm kiếm hoặc bộ lọc.
              </p>
            </div>
          )}

          {!loading &&
            !error &&
            filteredLinks.map((link) => (
              <article
                key={link.id}
                className="border-border hover:bg-muted/45 relative grid gap-3 border-b px-4 py-4 last:border-b-0 lg:grid-cols-[minmax(10rem,1.3fr)_minmax(12rem,1.5fr)_6rem_8rem_7rem] lg:items-center lg:gap-5 lg:px-5"
              >
                <div className="min-w-0">
                  <Link
                    href={`/links/${link.id}`}
                    className="text-primary block truncate font-mono text-sm font-semibold hover:underline"
                  >
                    {link.shortUrl}
                  </Link>
                  <p className="text-muted-foreground mt-1 truncate text-xs lg:hidden">
                    {destination(link.originalUrl)}
                  </p>
                </div>
                <p className="text-muted-foreground hidden truncate text-sm lg:block">
                  {destination(link.originalUrl)}
                </p>
                <p className="absolute top-4 right-4 text-sm font-semibold tabular-nums lg:static lg:text-right">
                  {number.format(link.clickCount ?? 0)}
                  <span className="text-muted-foreground ml-1 font-normal lg:hidden">
                    clicks
                  </span>
                </p>
                <span
                  className={cn(
                    "w-fit rounded-md px-2 py-1 text-xs font-semibold",
                    statusClass(link.status),
                  )}
                >
                  {statusLabel(link.status)}
                </span>
                <div className="flex items-center gap-1 lg:justify-end">
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon-sm"
                    aria-label={`Sao chép ${link.shortUrl}`}
                    onClick={() => copyLink(link)}
                  >
                    {copiedId === link.id ? <CheckIcon /> : <CopyIcon />}
                  </Button>
                  <Button asChild variant="ghost" size="icon-sm">
                    <Link
                      href={`/links/${link.id}`}
                      aria-label={`Xem analytics ${link.shortUrl}`}
                    >
                      <ChartLineUpIcon />
                    </Link>
                  </Button>
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon-sm"
                    aria-label={`Thao tác với ${link.shortUrl}`}
                    aria-expanded={menuId === link.id}
                    onClick={() =>
                      setMenuId(menuId === link.id ? null : link.id)
                    }
                  >
                    <DotsThreeIcon weight="bold" />
                  </Button>
                  {menuId === link.id && (
                    <div className="border-border bg-popover absolute right-4 bottom-3 z-10 w-48 rounded-xl border p-1.5 shadow-lg">
                      <Button
                        asChild
                        variant="ghost"
                        className="w-full justify-start"
                      >
                        <Link href={`/links/${link.id}`}>
                          <QrCodeIcon /> Mã QR và chi tiết
                        </Link>
                      </Button>
                      <Button
                        type="button"
                        variant="ghost"
                        className="w-full justify-start"
                        onClick={() =>
                          changeStatus(
                            link,
                            link.status === "ACTIVE" ? "ARCHIVED" : "ACTIVE",
                          )
                        }
                      >
                        {link.status === "ACTIVE"
                          ? "Tắt liên kết"
                          : "Bật lại liên kết"}
                      </Button>
                      <Button
                        type="button"
                        variant="destructive"
                        className="w-full justify-start"
                        onClick={() => changeStatus(link, "DELETED")}
                      >
                        Xóa liên kết
                      </Button>
                    </div>
                  )}
                </div>
              </article>
            ))}
        </div>
      </section>

      <Dialog open={createOpen} onOpenChange={setCreateOpen}>
        <DialogContent className="sm:max-w-xl">
          <div className="px-5 py-6 sm:px-6">
            <DialogHeader>
              <DialogTitle>Tạo short link</DialogTitle>
              <DialogDescription>
                Dán URL đích để tạo link. Bạn có thể mở tùy chọn nâng cao khi
                cần.
              </DialogDescription>
            </DialogHeader>
            <div className="mt-6">
              <CreateLinkSpotlight compact onCreated={addCreatedLink} />
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </main>
  );
}
