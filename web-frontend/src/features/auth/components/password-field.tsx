"use client";

import { EyeIcon, EyeSlashIcon } from "@phosphor-icons/react";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";

type PasswordFieldProps = React.ComponentProps<typeof Input>;

export function PasswordField({ className, ...props }: PasswordFieldProps) {
  const [visible, setVisible] = useState(false);
  const label = visible ? "Ẩn mật khẩu" : "Hiện mật khẩu";

  return (
    <div className="relative isolate">
      <Input
        {...props}
        type={visible ? "text" : "password"}
        className={cn("pr-12", className)}
      />
      <Button
        type="button"
        variant="ghost"
        size="icon-sm"
        className="text-muted-foreground hover:text-foreground absolute top-1/2 right-2 z-10 -translate-y-1/2"
        aria-label={label}
        title={label}
        style={{ zIndex: 1 }}
        onClick={() => setVisible((current) => !current)}
      >
        {visible ? <EyeSlashIcon /> : <EyeIcon />}
      </Button>
    </div>
  );
}
