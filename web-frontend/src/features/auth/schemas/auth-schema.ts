import { z } from "zod";

const emailSchema = z
  .string()
  .trim()
  .min(1, "Vui lòng nhập email.")
  .email("Email không đúng định dạng.");

const passwordSchema = z
  .string()
  .min(1, "Vui lòng nhập mật khẩu.")
  .min(8, "Mật khẩu cần có ít nhất 8 ký tự.")
  .max(128, "Mật khẩu không được vượt quá 128 ký tự.");

export const loginSchema = z.object({
  email: emailSchema,
  password: passwordSchema,
});

export const registerSchema = z
  .object({
    email: emailSchema,
    password: passwordSchema,
    confirmPassword: z.string().min(1, "Vui lòng xác nhận mật khẩu."),
  })
  .refine((values) => values.password === values.confirmPassword, {
    message: "Mật khẩu xác nhận không khớp.",
    path: ["confirmPassword"],
  });

export type LoginInput = z.infer<typeof loginSchema>;
export type RegisterInput = z.infer<typeof registerSchema>;
