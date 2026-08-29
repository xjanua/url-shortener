import type { Metadata } from "next";

import { RegisterForm } from "@/features/auth";

export const metadata: Metadata = {
  title: "Đăng ký",
  description: "Tạo tài khoản để bắt đầu quản lý và đo lường liên kết.",
};

export default function RegisterPage() {
  return <RegisterForm />;
}
