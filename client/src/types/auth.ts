export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
  notes?: string | null;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  message: string;
  accessToken: string;
  refreshToken: string;
  success: boolean;
}

export interface UnlockVaultRequest {
  masterPassword: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

export interface ApiSuccessResponse {
  success: boolean;
  message: string;
}

export interface ApiErrorResponse {
  code: string;
  message: string;
  details?: string | null;
}