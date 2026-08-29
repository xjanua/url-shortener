import type { ClientApiResponse } from "@/lib/api/contracts";
import { apiRequest } from "@/lib/api/client";
import type { LoginInput, RegisterInput } from "../schemas/auth-schema";
import type { LoginResponse } from "../types/auth";

async function submitAuth<T>(
  endpoint: string,
  values: LoginInput | RegisterInput,
): Promise<ClientApiResponse<T>> {
  return apiRequest<T>(endpoint, {
    method: "POST",
    body: JSON.stringify(values),
  });
}

export function login(values: LoginInput) {
  return submitAuth<LoginResponse>("/auth/login", values);
}

export function register(values: RegisterInput) {
  return submitAuth<null>("/auth/register", values);
}
