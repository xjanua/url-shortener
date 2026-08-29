import { AuthShell } from "@/features/auth";

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <AuthShell>{children}</AuthShell>;
}
