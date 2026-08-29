"use client";

import {
  BellIcon,
  ChartLineUpIcon,
  GearSixIcon,
  HouseIcon,
  LinkSimpleHorizontalIcon,
  QrCodeIcon,
  QuestionIcon,
  SignOutIcon,
} from "@phosphor-icons/react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";
import { apiRequest } from "@/lib/api/client";
import { cn } from "@/lib/utils";

const primaryNavigation = [
  { href: "/dashboard", label: "Dashboard", icon: HouseIcon },
  { href: "/links", label: "Links", icon: LinkSimpleHorizontalIcon },
  { href: "/analytics", label: "Analytics", icon: ChartLineUpIcon },
] as const;

const sidebarNavigation = [
  ...primaryNavigation,
  { href: "/qr-codes", label: "QR Codes", icon: QrCodeIcon },
] as const;

function isActive(pathname: string, href: string) {
  return pathname === href || pathname.startsWith(`${href}/`);
}

function Logo() {
  return (
    <Link
      href="/dashboard"
      className="focus-visible:ring-ring/35 flex items-center gap-2.5 rounded-lg font-bold tracking-tight focus-visible:ring-3 focus-visible:outline-none"
    >
      <span className="bg-primary text-primary-foreground grid size-9 place-items-center rounded-xl">
        <LinkSimpleHorizontalIcon className="size-4" weight="bold" />
      </span>
      <span className="hidden whitespace-nowrap sm:inline">URL Shortener</span>
    </Link>
  );
}

export function AppShell({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const [notificationsOpen, setNotificationsOpen] = useState(false);

  async function handleLogout() {
    setIsLoggingOut(true);
    await apiRequest<null>("/auth/logout", { method: "POST" });
    router.replace("/login");
    router.refresh();
  }

  return (
    <div className="product-shell bg-background text-foreground min-h-[100dvh]">
      <header className="border-border bg-card/95 fixed inset-x-0 top-0 z-30 h-16 border-b backdrop-blur">
        <div className="flex h-full items-center px-4 sm:px-6">
          <Logo />

          <div className="relative ml-auto flex items-center gap-2">
            <Button
              type="button"
              variant="ghost"
              size="icon-lg"
              className="rounded-full"
              aria-label="Thông báo"
              aria-expanded={notificationsOpen}
              onClick={() => setNotificationsOpen((open) => !open)}
            >
              <BellIcon />
            </Button>
            {notificationsOpen && (
              <div
                role="status"
                className="border-border bg-popover text-popover-foreground absolute top-12 right-0 w-64 rounded-xl border p-4 shadow-lg"
              >
                <p className="text-sm font-semibold">Thông báo</p>
                <p className="text-muted-foreground mt-1 text-sm">
                  Bạn chưa có thông báo mới.
                </p>
              </div>
            )}
            <button
              type="button"
              className="bg-primary text-primary-foreground focus-visible:ring-ring/35 grid size-9 place-items-center rounded-full text-sm font-bold focus-visible:ring-3 focus-visible:outline-none"
              aria-label="Tài khoản của Toàn"
              title="Toàn"
            >
              T
            </button>
          </div>
        </div>
      </header>

      <aside className="border-border bg-card fixed inset-y-0 top-16 left-0 z-20 hidden w-56 flex-col border-r px-4 py-6 md:flex">
        <nav className="grid gap-1" aria-label="Điều hướng bên">
          {sidebarNavigation.map(({ href, label, icon: Icon }) => {
            const active = isActive(pathname, href);
            return (
              <Link
                key={href}
                href={href}
                aria-current={active ? "page" : undefined}
                className={cn(
                  "focus-visible:ring-ring/35 flex h-10 items-center gap-3 rounded-lg px-3 text-sm font-medium transition-colors focus-visible:ring-3 focus-visible:outline-none",
                  active
                    ? "bg-accent text-accent-foreground"
                    : "text-muted-foreground hover:bg-muted hover:text-foreground",
                )}
              >
                <Icon
                  className="size-[1.125rem]"
                  weight={active ? "bold" : "regular"}
                />
                {label}
              </Link>
            );
          })}
        </nav>

        <div className="border-border mt-6 border-t pt-6">
          <Link
            href="/settings"
            aria-current={isActive(pathname, "/settings") ? "page" : undefined}
            className={cn(
              "flex h-10 items-center gap-3 rounded-lg px-3 text-sm font-medium transition-colors",
              isActive(pathname, "/settings")
                ? "bg-accent text-accent-foreground"
                : "text-muted-foreground hover:bg-muted hover:text-foreground",
            )}
          >
            <GearSixIcon className="size-[1.125rem]" />
            Settings
          </Link>
          <Link
            href="/help"
            aria-current={isActive(pathname, "/help") ? "page" : undefined}
            className={cn(
              "mt-1 flex h-10 items-center gap-3 rounded-lg px-3 text-sm font-medium transition-colors",
              isActive(pathname, "/help")
                ? "bg-accent text-accent-foreground"
                : "text-muted-foreground hover:bg-muted hover:text-foreground",
            )}
          >
            <QuestionIcon className="size-[1.125rem]" />
            Help
          </Link>
        </div>

        <div className="border-border mt-auto border-t pt-4">
          <Button
            type="button"
            variant="ghost"
            className="text-muted-foreground hover:text-foreground w-full justify-start px-3"
            disabled={isLoggingOut}
            onClick={handleLogout}
          >
            {isLoggingOut ? <Spinner /> : <SignOutIcon />}
            {isLoggingOut ? "Đang đăng xuất" : "Đăng xuất"}
          </Button>
        </div>
      </aside>

      <div className="pt-16 pb-20 md:pb-0 md:pl-56">{children}</div>

      <nav
        className="border-border bg-card/95 fixed inset-x-0 bottom-0 z-20 grid h-16 grid-cols-4 border-t px-2 backdrop-blur md:hidden"
        aria-label="Điều hướng di động"
      >
        {[
          ...primaryNavigation,
          { href: "/settings", label: "Settings", icon: GearSixIcon },
        ].map(({ href, label, icon: Icon }) => {
          const active = isActive(pathname, href);
          return (
            <Link
              key={href}
              href={href}
              className={cn(
                "flex flex-col items-center justify-center gap-1 text-[0.68rem] font-medium",
                active ? "text-primary" : "text-muted-foreground",
              )}
            >
              <Icon className="size-5" weight={active ? "bold" : "regular"} />
              {label}
            </Link>
          );
        })}
      </nav>
    </div>
  );
}
