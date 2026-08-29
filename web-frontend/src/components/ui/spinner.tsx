import { cn } from "@/lib/utils";
import { SpinnerIcon } from "@phosphor-icons/react";

function Spinner({ className }: { className?: string }) {
  return (
    <SpinnerIcon
      data-slot="spinner"
      role="status"
      aria-label="Đang tải"
      className={cn("size-4 animate-spin", className)}
    />
  );
}

export { Spinner };
