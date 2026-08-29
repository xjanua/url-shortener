import { MonitorIcon, ShieldCheckIcon } from "@phosphor-icons/react/dist/ssr";
import type { Metadata } from "next";

export const metadata: Metadata = { title: "Cài đặt" };

export default function SettingsPage() {
  return (
    <main className="min-h-[100dvh] px-4 py-10 sm:px-6 md:px-10 md:py-14">
      <div className="mx-auto max-w-4xl">
        <p className="text-primary text-sm font-semibold">Tài khoản</p>
        <h1 className="mt-2 text-3xl font-semibold tracking-[-0.04em]">
          Cài đặt
        </h1>
        <p className="text-muted-foreground mt-2 text-sm leading-6">
          Kiểm soát trải nghiệm sử dụng và phiên đăng nhập của bạn.
        </p>

        <div className="divide-border border-border bg-card mt-10 divide-y rounded-xl border">
          <section className="flex gap-4 p-5 sm:p-6">
            <span className="bg-accent text-accent-foreground grid size-10 shrink-0 place-items-center rounded-lg">
              <MonitorIcon className="size-5" weight="bold" />
            </span>
            <div>
              <h2 className="font-semibold">Giao diện</h2>
              <p className="text-muted-foreground mt-1 text-sm leading-6">
                Màu sắc tự động đồng bộ với chế độ sáng hoặc tối trên thiết bị
                của bạn.
              </p>
            </div>
          </section>
          <section className="flex gap-4 p-5 sm:p-6">
            <span className="bg-accent text-accent-foreground grid size-10 shrink-0 place-items-center rounded-lg">
              <ShieldCheckIcon className="size-5" weight="bold" />
            </span>
            <div>
              <h2 className="font-semibold">Phiên đăng nhập</h2>
              <p className="text-muted-foreground mt-1 text-sm leading-6">
                Phiên làm việc được lưu bằng cookie bảo mật. Bạn có thể đăng
                xuất từ thanh điều hướng bất cứ lúc nào.
              </p>
            </div>
          </section>
        </div>
      </div>
    </main>
  );
}
