"use client";

import { XIcon } from "@phosphor-icons/react";
import { Dialog as DialogPrimitive } from "radix-ui";

import { cn } from "@/lib/utils";

const Dialog = DialogPrimitive.Root;
const DialogTrigger = DialogPrimitive.Trigger;
const DialogClose = DialogPrimitive.Close;

function DialogContent({
  className,
  children,
  showCloseButton = true,
  ...props
}: React.ComponentProps<typeof DialogPrimitive.Content> & {
  showCloseButton?: boolean;
}) {
  return (
    <DialogPrimitive.Portal>
      <DialogPrimitive.Overlay className="bg-foreground/30 motion-safe:animate-in motion-safe:fade-in-0 fixed inset-0 z-40 backdrop-blur-[2px]" />
      <DialogPrimitive.Content
        data-slot="dialog-content"
        className={cn(
          "border-border bg-popover text-popover-foreground motion-safe:animate-in motion-safe:slide-in-from-bottom-4 sm:motion-safe:zoom-in-95 fixed inset-x-0 bottom-0 z-50 max-h-[92dvh] overflow-y-auto rounded-t-2xl border shadow-2xl outline-none sm:top-1/2 sm:bottom-auto sm:left-1/2 sm:w-[calc(100%-2rem)] sm:max-w-2xl sm:-translate-x-1/2 sm:-translate-y-1/2 sm:rounded-2xl",
          className,
        )}
        {...props}
      >
        {children}
        {showCloseButton && (
          <DialogPrimitive.Close className="text-muted-foreground hover:bg-muted hover:text-foreground focus-visible:ring-ring/35 absolute top-4 right-4 grid size-8 place-items-center rounded-lg transition-colors focus-visible:ring-3 focus-visible:outline-none">
            <XIcon />
            <span className="sr-only">Đóng</span>
          </DialogPrimitive.Close>
        )}
      </DialogPrimitive.Content>
    </DialogPrimitive.Portal>
  );
}

function DialogHeader({ className, ...props }: React.ComponentProps<"div">) {
  return <div className={cn("space-y-1.5 text-left", className)} {...props} />;
}

function DialogFooter({ className, ...props }: React.ComponentProps<"div">) {
  return (
    <div
      className={cn(
        "border-border bg-popover/95 sticky bottom-0 flex flex-col-reverse gap-2 border-t px-5 py-4 backdrop-blur sm:flex-row sm:justify-end sm:px-6",
        className,
      )}
      {...props}
    />
  );
}

function DialogTitle({
  className,
  ...props
}: React.ComponentProps<typeof DialogPrimitive.Title>) {
  return (
    <DialogPrimitive.Title
      className={cn("text-xl font-semibold tracking-tight", className)}
      {...props}
    />
  );
}

function DialogDescription({
  className,
  ...props
}: React.ComponentProps<typeof DialogPrimitive.Description>) {
  return (
    <DialogPrimitive.Description
      className={cn("text-muted-foreground text-sm leading-6", className)}
      {...props}
    />
  );
}

export {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
};
