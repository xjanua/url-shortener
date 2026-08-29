"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import {
  Field,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Spinner } from "@/components/ui/spinner";
import { register } from "../api/auth-client";
import { registerSchema, type RegisterInput } from "../schemas/auth-schema";
import { AuthStatus } from "./auth-status";
import { PasswordField } from "./password-field";

const errorMessages: Record<string, string> = {
  EMAIL_ALREADY_EXISTS: "Email này đã được sử dụng.",
  PASSWORD_MISMATCH: "Mật khẩu xác nhận không khớp.",
};

export function RegisterForm() {
  const router = useRouter();
  const [serverError, setServerError] = useState<string | null>(null);
  const form = useForm<RegisterInput>({
    resolver: zodResolver(registerSchema),
    defaultValues: { email: "", password: "", confirmPassword: "" },
  });

  async function onSubmit(values: RegisterInput) {
    setServerError(null);
    const response = await register(values);

    if (!response.success) {
      setServerError(
        errorMessages[response.error.code] ?? response.error.message,
      );
      return;
    }

    router.push("/login?registered=1");
  }

  return (
    <div>
      <div className="mb-8">
        <p className="text-primary mb-3 text-sm font-semibold">Đăng ký</p>
        <h1 className="text-3xl font-semibold tracking-[-0.035em] text-balance">
          Tạo tài khoản của bạn
        </h1>
        <p className="text-muted-foreground mt-3 text-sm leading-6">
          Bắt đầu tạo liên kết và xem dữ liệu truy cập trong vài phút.
        </p>
      </div>

      <form onSubmit={form.handleSubmit(onSubmit)} noValidate>
        <FieldGroup>
          {serverError && <AuthStatus tone="error">{serverError}</AuthStatus>}

          <Field data-invalid={Boolean(form.formState.errors.email)}>
            <FieldLabel htmlFor="register-email">Email</FieldLabel>
            <Input
              id="register-email"
              type="email"
              autoComplete="email"
              inputMode="email"
              placeholder="ban@congty.vn"
              aria-invalid={Boolean(form.formState.errors.email)}
              aria-describedby="register-email-error"
              {...form.register("email")}
            />
            <FieldError
              id="register-email-error"
              errors={[form.formState.errors.email]}
            />
          </Field>

          <Field data-invalid={Boolean(form.formState.errors.password)}>
            <FieldLabel htmlFor="register-password">Mật khẩu</FieldLabel>
            <PasswordField
              id="register-password"
              autoComplete="new-password"
              placeholder="Tạo mật khẩu"
              aria-invalid={Boolean(form.formState.errors.password)}
              aria-describedby="register-password-help register-password-error"
              {...form.register("password")}
            />
            <FieldDescription id="register-password-help">
              Sử dụng ít nhất 8 ký tự.
            </FieldDescription>
            <FieldError
              id="register-password-error"
              errors={[form.formState.errors.password]}
            />
          </Field>

          <Field data-invalid={Boolean(form.formState.errors.confirmPassword)}>
            <FieldLabel htmlFor="register-confirm-password">
              Xác nhận mật khẩu
            </FieldLabel>
            <PasswordField
              id="register-confirm-password"
              autoComplete="new-password"
              placeholder="Nhập lại mật khẩu"
              aria-invalid={Boolean(form.formState.errors.confirmPassword)}
              aria-describedby="register-confirm-password-error"
              {...form.register("confirmPassword")}
            />
            <FieldError
              id="register-confirm-password-error"
              errors={[form.formState.errors.confirmPassword]}
            />
          </Field>

          <Button
            type="submit"
            size="lg"
            className="w-full"
            disabled={form.formState.isSubmitting}
          >
            {form.formState.isSubmitting && <Spinner />}
            {form.formState.isSubmitting
              ? "Đang tạo tài khoản"
              : "Tạo tài khoản"}
          </Button>
        </FieldGroup>
      </form>

      <p className="text-muted-foreground mt-7 text-center text-sm">
        Đã có tài khoản?{" "}
        <Link
          href="/login"
          className="text-primary focus-visible:ring-ring/35 font-semibold underline-offset-4 hover:underline focus-visible:rounded-sm focus-visible:ring-3 focus-visible:outline-none"
        >
          Đăng nhập
        </Link>
      </p>
    </div>
  );
}
