"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Spinner } from "@/components/ui/spinner";
import { login } from "../api/auth-client";
import { loginSchema, type LoginInput } from "../schemas/auth-schema";
import { AuthStatus } from "./auth-status";
import { PasswordField } from "./password-field";

const errorMessages: Record<string, string> = {
  INVALID_CREDENTIALS: "Email hoặc mật khẩu không đúng.",
  USER_NOT_FOUND: "Email hoặc mật khẩu không đúng.",
};

export function LoginForm({ registered = false }: { registered?: boolean }) {
  const router = useRouter();
  const [serverError, setServerError] = useState<string | null>(null);
  const form = useForm<LoginInput>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: "", password: "" },
  });

  async function onSubmit(values: LoginInput) {
    setServerError(null);
    const response = await login(values);

    if (!response.success) {
      setServerError(
        errorMessages[response.error.code] ?? response.error.message,
      );
      return;
    }

    router.replace("/dashboard");
    router.refresh();
  }

  return (
    <div>
      <div className="mb-8">
        <p className="text-primary mb-3 text-sm font-semibold">Đăng nhập</p>
        <h1 className="text-3xl font-semibold tracking-[-0.035em] text-balance">
          Chào mừng trở lại
        </h1>
        <p className="text-muted-foreground mt-3 text-sm leading-6">
          Quản lý liên kết và theo dõi hiệu quả từ một nơi duy nhất.
        </p>
      </div>

      <form onSubmit={form.handleSubmit(onSubmit)} noValidate>
        <FieldGroup>
          {registered && (
            <AuthStatus tone="success">
              Tài khoản đã được tạo. Bạn có thể đăng nhập ngay.
            </AuthStatus>
          )}
          {serverError && <AuthStatus tone="error">{serverError}</AuthStatus>}
          <Field data-invalid={Boolean(form.formState.errors.email)}>
            <FieldLabel htmlFor="login-email">Email</FieldLabel>
            <Input
              id="login-email"
              type="email"
              autoComplete="email"
              inputMode="email"
              placeholder="ban@congty.vn"
              aria-invalid={Boolean(form.formState.errors.email)}
              aria-describedby="login-email-error"
              {...form.register("email")}
            />
            <FieldError
              id="login-email-error"
              errors={[form.formState.errors.email]}
            />
          </Field>

          <Field data-invalid={Boolean(form.formState.errors.password)}>
            <FieldLabel htmlFor="login-password">Mật khẩu</FieldLabel>
            <PasswordField
              id="login-password"
              autoComplete="current-password"
              placeholder="Nhập mật khẩu"
              aria-invalid={Boolean(form.formState.errors.password)}
              aria-describedby="login-password-error"
              {...form.register("password")}
            />
            <FieldError
              id="login-password-error"
              errors={[form.formState.errors.password]}
            />
          </Field>

          <Button
            type="submit"
            size="lg"
            className="w-full"
            disabled={form.formState.isSubmitting}
          >
            {form.formState.isSubmitting && <Spinner />}
            {form.formState.isSubmitting ? "Đang đăng nhập" : "Đăng nhập"}
          </Button>
        </FieldGroup>
      </form>

      <p className="text-muted-foreground mt-7 text-center text-sm">
        Chưa có tài khoản?{" "}
        <Link
          href="/register"
          className="text-primary focus-visible:ring-ring/35 font-semibold underline-offset-4 hover:underline focus-visible:rounded-sm focus-visible:ring-3 focus-visible:outline-none"
        >
          Đăng ký
        </Link>
      </p>
    </div>
  );
}
