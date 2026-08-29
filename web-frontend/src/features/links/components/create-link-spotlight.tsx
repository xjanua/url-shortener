"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import {
  ArrowRightIcon,
  CheckIcon,
  CopyIcon,
  LinkSimpleIcon,
  SlidersHorizontalIcon,
} from "@phosphor-icons/react";
import { useState } from "react";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Spinner } from "@/components/ui/spinner";
import { createLink } from "../api/link-client";
import { createLinkSchema, type CreateLinkInput } from "../schemas/link-schema";
import type { ShortLink } from "../types/link";
import { AdvancedLinkDialog } from "./advanced-link-dialog";

export function CreateLinkSpotlight({
  onCreated,
  compact = false,
}: {
  onCreated: (link: ShortLink) => void;
  compact?: boolean;
}) {
  const [serverError, setServerError] = useState<string | null>(null);
  const [createdLink, setCreatedLink] = useState<ShortLink | null>(null);
  const [copied, setCopied] = useState(false);
  const [advancedOpen, setAdvancedOpen] = useState(false);
  const quickForm = useForm<CreateLinkInput>({
    resolver: zodResolver(createLinkSchema),
    defaultValues: { originalUrl: "" },
  });
  const advancedForm = useForm<CreateLinkInput>({
    resolver: zodResolver(createLinkSchema),
    defaultValues: {
      originalUrl: "",
      title: "",
      shortCode: "",
      password: "",
      expiresAt: "",
      androidUrl: "",
      iosUrl: "",
      desktopUrl: "",
    },
  });

  async function submitLink(values: CreateLinkInput) {
    setServerError(null);
    setCreatedLink(null);
    const response = await createLink(values);
    if (!response.success) {
      setServerError(response.error.message);
      return false;
    }
    setCreatedLink(response.data);
    onCreated(response.data);
    quickForm.reset();
    advancedForm.reset();
    return true;
  }

  async function submitAdvancedLink(values: CreateLinkInput) {
    if (await submitLink(values)) setAdvancedOpen(false);
  }

  function openAdvancedOptions() {
    setServerError(null);
    const quickUrl = quickForm.getValues("originalUrl");
    if (quickUrl) advancedForm.setValue("originalUrl", quickUrl);
    setAdvancedOpen(true);
  }

  async function copyCreatedLink() {
    if (!createdLink) return;
    await navigator.clipboard.writeText(createdLink.shortUrl);
    setCopied(true);
    window.setTimeout(() => setCopied(false), 1800);
  }

  return (
    <section
      className={
        compact
          ? "w-full text-left"
          : "border-border w-full border-b pb-9 text-left"
      }
    >
      {!compact && (
        <div>
          <h1 className="text-3xl font-semibold tracking-[-0.045em] text-balance sm:text-4xl">
            Tạo liên kết mới
          </h1>
          <p className="text-muted-foreground mt-2 text-sm leading-6">
            Dán URL đích, thiết lập tùy chọn nếu cần và tạo link ngay.
          </p>
        </div>
      )}

      <form
        className={compact ? "" : "mt-7"}
        onSubmit={quickForm.handleSubmit((values) =>
          submitLink({ originalUrl: values.originalUrl }),
        )}
        noValidate
      >
        <div className="border-border bg-card focus-within:border-ring focus-within:ring-ring/15 grid gap-2 rounded-2xl border p-2 shadow-[0_18px_50px_-40px_#183042] focus-within:ring-3 sm:grid-cols-[minmax(0,1fr)_auto]">
          <div className="relative min-w-0">
            <LinkSimpleIcon className="text-muted-foreground absolute top-1/2 left-3.5 size-5 -translate-y-1/2" />
            <label htmlFor="create-link-url" className="sr-only">
              URL cần rút gọn
            </label>
            <Input
              id="create-link-url"
              type="url"
              inputMode="url"
              autoComplete="url"
              placeholder="https://example.com/duong-dan-dai"
              className="h-12 border-0 bg-transparent pl-11 shadow-none focus-visible:ring-0"
              aria-invalid={Boolean(quickForm.formState.errors.originalUrl)}
              aria-describedby="create-link-error"
              {...quickForm.register("originalUrl")}
            />
          </div>
          <Button
            type="submit"
            size="lg"
            className="bg-primary text-primary-foreground hover:bg-primary/90 w-full rounded-xl sm:w-auto"
            disabled={quickForm.formState.isSubmitting}
          >
            {quickForm.formState.isSubmitting ? (
              <Spinner />
            ) : (
              <ArrowRightIcon />
            )}
            {quickForm.formState.isSubmitting ? "Đang tạo" : "Tạo link"}
          </Button>
        </div>

        {(quickForm.formState.errors.originalUrl?.message || serverError) && (
          <p
            id="create-link-error"
            role="alert"
            className="text-destructive mt-3 text-sm"
          >
            {quickForm.formState.errors.originalUrl?.message ?? serverError}
          </p>
        )}

        <Button
          type="button"
          variant="ghost"
          className="text-muted-foreground hover:bg-muted hover:text-foreground mt-3 rounded-xl"
          onClick={openAdvancedOptions}
        >
          <SlidersHorizontalIcon />
          Tùy chọn nâng cao
        </Button>
      </form>

      <AdvancedLinkDialog
        open={advancedOpen}
        onOpenChange={setAdvancedOpen}
        form={advancedForm}
        onSubmit={submitAdvancedLink}
        serverError={serverError}
      />

      {createdLink && (
        <div
          role="status"
          className="border-border bg-card mt-5 flex flex-col gap-3 rounded-xl border p-4 text-left sm:flex-row sm:items-center"
        >
          <span className="bg-success text-success-foreground grid size-9 shrink-0 place-items-center rounded-lg">
            <CheckIcon className="size-4" weight="bold" />
          </span>
          <div className="min-w-0 flex-1">
            <p className="text-sm font-semibold">Liên kết đã sẵn sàng</p>
            <p className="text-primary mt-0.5 truncate font-mono text-sm">
              {createdLink.shortUrl}
            </p>
          </div>
          <Button
            type="button"
            variant="outline"
            className="shrink-0"
            onClick={copyCreatedLink}
          >
            {copied ? <CheckIcon /> : <CopyIcon />}
            {copied ? "Đã sao chép" : "Sao chép"}
          </Button>
        </div>
      )}
    </section>
  );
}
