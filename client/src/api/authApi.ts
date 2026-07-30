import { httpClient } from './httpClient';
import type {
  ApiSuccessResponse,
  ChangePasswordRequest,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
} from '../types/auth';

export const authApi = {
  register: (data: RegisterRequest) =>
    httpClient.post<ApiSuccessResponse>('/auth/register', data),

  login: (data: LoginRequest) =>
    httpClient.post<LoginResponse>('/auth/login', data),

  changePassword: (data: ChangePasswordRequest) =>
    httpClient.post<ApiSuccessResponse>('/auth/change-password', data),

  logout: () => httpClient.post<ApiSuccessResponse>('/auth/logout', {}),
};