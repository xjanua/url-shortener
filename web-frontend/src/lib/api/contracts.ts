export type ApiError = {
  code: string;
  message: string;
};

export type RestResponse<T> = {
  success: boolean;
  error: ApiError | null;
  data: T | null;
};

export type ClientApiResponse<T = null> =
  { success: true; data: T } | { success: false; error: ApiError };
