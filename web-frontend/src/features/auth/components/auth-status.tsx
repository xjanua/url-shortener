import {
  CheckCircleIcon,
  WarningCircleIcon,
} from "@phosphor-icons/react/dist/ssr";

import { cn } from "@/lib/utils";

export function AuthStatus({
  tone,
  children,
}: {
  tone: "error" | "success";
  children: React.ReactNode;
}) {
  const Icon = tone === "success" ? CheckCircleIcon : WarningCircleIcon;

  return (
    <div
      role={tone === "error" ? "alert" : "status"}
      className={cn(
        "flex items-start gap-2.5 rounded-lg border px-3.5 py-3 text-sm leading-5",
        tone === "success"
          ? "border-success/25 bg-success/8 text-success"
          : "border-destructive/25 bg-destructive/8 text-destructive",
      )}
    >
      <Icon className="mt-0.5 size-4 shrink-0" weight="fill" />
      <span>{children}</span>
    </div>
  );
}
