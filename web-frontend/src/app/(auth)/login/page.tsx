import type { Metadata } from "next";

import { LoginForm } from "@/features/auth";

export const metadata: Metadata = {
  title: "Đăng nhập",
  description: "Đăng nhập để quản lý liên kết và theo dõi lượt truy cập.",
};

export default async function LoginPage({
  searchParams,
}: {
  searchParams: Promise<{ registered?: string | string[] }>;
}) {
  const params = await searchParams;
  return <LoginForm registered={params.registered === "1"} />;
}
