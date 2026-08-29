import type { Metadata } from "next";

import { ChatCircleTextIcon } from "@phosphor-icons/react/dist/ssr";

import { Button } from "@/components/ui/button";

export const metadata: Metadata = { title: "Trợ giúp" };

export default function HelpPage() {
  return (
    <main className="mx-auto grid min-h-[100dvh] max-w-6xl place-items-center px-4 py-10 sm:px-6 md:px-10">
      <section className="border-border bg-card w-full max-w-xl rounded-xl border p-7 text-center sm:p-10">
        <span className="bg-accent text-accent-foreground mx-auto grid size-12 place-items-center rounded-xl">
          <ChatCircleTextIcon className="size-6" weight="bold" />
        </span>
        <h1 className="mt-5 text-2xl font-semibold tracking-tight">Trợ giúp</h1>
        <p className="text-muted-foreground mt-2 text-sm leading-6">
          Trung tâm trợ giúp đang được hoàn thiện. Bạn có thể tiếp tục tạo và
          quản lý short links bình thường.
        </p>
        <Button asChild className="mt-6">
          <a href="mailto:support@example.com">Liên hệ hỗ trợ</a>
        </Button>
      </section>
    </main>
  );
}
