import { QrCodeIcon } from "@phosphor-icons/react/dist/ssr";
import type { Metadata } from "next";

export const metadata: Metadata = { title: "QR Codes" };

export default function QrCodesPage() {
  return (
    <main className="min-h-[100dvh] px-4 py-8 sm:px-6 md:px-8 lg:px-10">
      <div className="mx-auto max-w-7xl">
        <h1 className="text-3xl font-semibold tracking-[-0.04em]">QR Codes</h1>
        <p className="text-muted-foreground mt-2 text-sm">
          Tạo và quản lý QR code cho các liên kết của bạn.
        </p>
        <div className="border-border bg-card mt-8 rounded-xl border px-5 py-14 text-center">
          <span className="bg-accent text-accent-foreground mx-auto grid size-12 place-items-center rounded-xl">
            <QrCodeIcon className="size-6" weight="bold" />
          </span>
          <h2 className="mt-4 font-semibold">Chức năng đang được chuẩn bị</h2>
          <p className="text-muted-foreground mx-auto mt-1 max-w-md text-sm leading-6">
            QR Codes sẽ được phát triển sau khi luồng tạo link và thống kê MVP
            hoàn thiện.
          </p>
        </div>
      </div>
    </main>
  );
}
