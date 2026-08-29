import * as React from "react";

import { cn } from "@/lib/utils";

function Input({ className, type, ...props }: React.ComponentProps<"input">) {
  return (
    <input
      type={type}
      data-slot="input"
      className={cn(
        "border-input bg-card file:text-foreground placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-ring/25 disabled:bg-muted aria-invalid:border-destructive aria-invalid:ring-destructive/15 h-11 w-full min-w-0 rounded-lg border px-3.5 py-2 text-base shadow-xs transition-[border-color,box-shadow,background-color] outline-none file:inline-flex file:h-7 file:border-0 file:bg-transparent file:text-sm file:font-medium focus-visible:ring-3 disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-60 aria-invalid:ring-3 md:text-sm",
        className,
      )}
      {...props}
    />
  );
}

export { Input };
