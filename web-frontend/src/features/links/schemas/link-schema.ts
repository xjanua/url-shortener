import { z } from "zod";

const optionalUrl = z
  .string()
  .trim()
  .max(2048, "URL không được dài quá 2048 ký tự.")
  .refine((value) => !value || URL.canParse(value), "URL chưa đúng định dạng.")
  .optional();

export const createLinkSchema = z.object({
  originalUrl: z
    .string()
    .trim()
    .min(1, "Hãy dán URL bạn muốn rút gọn.")
    .url("URL chưa đúng định dạng. Ví dụ: https://example.com"),
  title: z
    .string()
    .trim()
    .max(120, "Tiêu đề không được dài quá 120 ký tự.")
    .optional(),
  shortCode: z
    .string()
    .trim()
    .max(32, "Mã tùy chỉnh không được dài quá 32 ký tự.")
    .regex(/^[A-Za-z0-9_-]*$/, "Chỉ dùng chữ, số, dấu gạch ngang và gạch dưới.")
    .optional(),
  password: z
    .string()
    .max(128, "Mật khẩu không được dài quá 128 ký tự.")
    .optional(),
  expiresAt: z
    .string()
    .optional()
    .refine(
      (value) => !value || new Date(value).getTime() > Date.now(),
      "Thời hạn phải nằm trong tương lai.",
    ),
  androidUrl: optionalUrl,
  iosUrl: optionalUrl,
  desktopUrl: optionalUrl,
});

export type CreateLinkInput = z.infer<typeof createLinkSchema>;
