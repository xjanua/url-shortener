"use client";

import {
  CalendarBlankIcon,
  CaretDownIcon,
  DeviceMobileIcon,
  LinkSimpleIcon,
  LockKeyIcon,
  SlidersHorizontalIcon,
} from "@phosphor-icons/react";
import type { UseFormReturn } from "react-hook-form";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Field,
  FieldDescription,
  FieldError,
  FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Spinner } from "@/components/ui/spinner";
import type { CreateLinkInput } from "../schemas/link-schema";

type AdvancedLinkDialogProps = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  form: UseFormReturn<CreateLinkInput>;
  onSubmit: (values: CreateLinkInput) => Promise<void>;
  serverError: string | null;
};

export function AdvancedLinkDialog({
  open,
  onOpenChange,
  form,
  onSubmit,
  serverError,
}: AdvancedLinkDialogProps) {
  const errors = form.formState.errors;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <form onSubmit={form.handleSubmit(onSubmit)} noValidate>
          <div className="px-5 pt-6 pb-5 sm:px-6">
            <DialogHeader className="pr-10">
              <span className="bg-accent text-accent-foreground mb-2 grid size-10 place-items-center rounded-xl">
                <SlidersHorizontalIcon className="size-5" weight="bold" />
              </span>
              <DialogTitle>Tùy chọn nâng cao</DialogTitle>
              <DialogDescription>
                Kiểm soát cách liên kết hiển thị, được bảo vệ và chuyển hướng.
              </DialogDescription>
            </DialogHeader>

            <div className="mt-7 space-y-7">
              <fieldset className="space-y-4">
                <legend className="mb-4 flex items-center gap-2 text-sm font-semibold">
                  <LinkSimpleIcon className="text-primary size-4" />
                  Thông tin liên kết
                </legend>

                <Field data-invalid={Boolean(errors.originalUrl)}>
                  <FieldLabel htmlFor="advanced-original-url">
                    URL đích
                  </FieldLabel>
                  <Input
                    id="advanced-original-url"
                    type="url"
                    inputMode="url"
                    autoComplete="url"
                    placeholder="https://example.com/duong-dan-dai"
                    aria-invalid={Boolean(errors.originalUrl)}
                    {...form.register("originalUrl")}
                  />
                  <FieldError errors={[errors.originalUrl]} />
                </Field>

                <div className="grid gap-4 sm:grid-cols-2">
                  <Field data-invalid={Boolean(errors.title)}>
                    <FieldLabel htmlFor="advanced-title">Tiêu đề</FieldLabel>
                    <Input
                      id="advanced-title"
                      placeholder="Chiến dịch mùa hè"
                      aria-invalid={Boolean(errors.title)}
                      {...form.register("title")}
                    />
                    <FieldDescription>
                      Giúp bạn nhận ra liên kết trong danh sách.
                    </FieldDescription>
                    <FieldError errors={[errors.title]} />
                  </Field>

                  <Field data-invalid={Boolean(errors.shortCode)}>
                    <FieldLabel htmlFor="advanced-short-code">
                      Mã tùy chỉnh
                    </FieldLabel>
                    <Input
                      id="advanced-short-code"
                      autoCapitalize="none"
                      spellCheck={false}
                      placeholder="khuyen-mai"
                      aria-invalid={Boolean(errors.shortCode)}
                      {...form.register("shortCode")}
                    />
                    <FieldDescription>
                      Để trống nếu muốn hệ thống tự tạo.
                    </FieldDescription>
                    <FieldError errors={[errors.shortCode]} />
                  </Field>
                </div>
              </fieldset>

              <fieldset className="border-border space-y-4 border-t pt-6">
                <legend className="flex items-center gap-2 pr-3 text-sm font-semibold">
                  <LockKeyIcon className="text-primary size-4" />
                  Bảo vệ và thời hạn
                </legend>
                <div className="grid gap-4 sm:grid-cols-2">
                  <Field data-invalid={Boolean(errors.password)}>
                    <FieldLabel htmlFor="advanced-password">
                      Mật khẩu
                    </FieldLabel>
                    <Input
                      id="advanced-password"
                      type="password"
                      autoComplete="new-password"
                      placeholder="Không bắt buộc"
                      aria-invalid={Boolean(errors.password)}
                      {...form.register("password")}
                    />
                    <FieldDescription>
                      Người mở link phải nhập mật khẩu này.
                    </FieldDescription>
                    <FieldError errors={[errors.password]} />
                  </Field>

                  <Field data-invalid={Boolean(errors.expiresAt)}>
                    <FieldLabel htmlFor="advanced-expires-at">
                      <CalendarBlankIcon />
                      Thời hạn
                    </FieldLabel>
                    <Input
                      id="advanced-expires-at"
                      type="datetime-local"
                      aria-invalid={Boolean(errors.expiresAt)}
                      {...form.register("expiresAt")}
                    />
                    <FieldDescription>
                      Để trống nếu liên kết không hết hạn.
                    </FieldDescription>
                    <FieldError errors={[errors.expiresAt]} />
                  </Field>
                </div>
              </fieldset>

              <details className="group border-border border-t pt-6">
                <summary className="focus-visible:ring-ring/35 flex cursor-pointer list-none items-center gap-3 rounded-lg text-left focus-visible:ring-3 focus-visible:outline-none [&::-webkit-details-marker]:hidden">
                  <span className="bg-muted text-muted-foreground grid size-9 place-items-center rounded-lg">
                    <DeviceMobileIcon className="size-[1.125rem]" />
                  </span>
                  <span className="min-w-0 flex-1">
                    <span className="block text-sm font-semibold">
                      Định tuyến theo thiết bị
                    </span>
                    <span className="text-muted-foreground mt-0.5 block text-xs">
                      Dùng URL riêng cho Android, iOS hoặc máy tính.
                    </span>
                  </span>
                  <CaretDownIcon className="text-muted-foreground size-4 transition-transform group-open:rotate-180" />
                </summary>

                <div className="mt-5 grid gap-4">
                  {(
                    [
                      [
                        "androidUrl",
                        "URL Android",
                        "https://play.google.com/store/apps/...",
                      ],
                      ["iosUrl", "URL iOS", "https://apps.apple.com/app/..."],
                      [
                        "desktopUrl",
                        "URL máy tính",
                        "https://example.com/desktop",
                      ],
                    ] as const
                  ).map(([name, label, placeholder]) => (
                    <Field key={name} data-invalid={Boolean(errors[name])}>
                      <FieldLabel htmlFor={`advanced-${name}`}>
                        {label}
                      </FieldLabel>
                      <Input
                        id={`advanced-${name}`}
                        type="url"
                        inputMode="url"
                        placeholder={placeholder}
                        aria-invalid={Boolean(errors[name])}
                        {...form.register(name)}
                      />
                      <FieldError errors={[errors[name]]} />
                    </Field>
                  ))}
                </div>
              </details>

              {serverError && (
                <p role="alert" className="text-destructive text-sm">
                  {serverError}
                </p>
              )}
            </div>
          </div>

          <DialogFooter>
            <DialogClose asChild>
              <Button type="button" variant="ghost">
                Hủy
              </Button>
            </DialogClose>
            <Button type="submit" disabled={form.formState.isSubmitting}>
              {form.formState.isSubmitting ? <Spinner /> : <LinkSimpleIcon />}
              {form.formState.isSubmitting ? "Đang tạo" : "Tạo liên kết"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
