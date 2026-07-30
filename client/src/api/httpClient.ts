import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '../store/authStore';

const BASE_URL = 'https://localhost:8443';

export const httpClient = axios.create({
  baseURL: BASE_URL,
});

httpClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const accessToken = useAuthStore.getState().accessToken;
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

let isRefreshing = false;
let pendingRequests: Array<() => void> = [];

httpClient.interceptors.response.use(
  (response) => {
    const data = response.data;
    const looksLikeError =
      data &&
      typeof data === 'object' &&
      !Array.isArray(data) &&
      'code' in data &&
      'message' in data &&
      !('success' in data);

    if (looksLikeError) {
      return Promise.reject({ isApiError: true, apiError: data, response });
    }

    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    const isUnauthorized = error.response?.status === 401;
    const isAuthEndpoint = originalRequest?.url?.includes('/auth/');

    if (isUnauthorized && !originalRequest._retry && !isAuthEndpoint) {
      originalRequest._retry = true;

      const refreshToken = useAuthStore.getState().refreshToken;
      if (!refreshToken) {
        useAuthStore.getState().clearSession();
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise((resolve) => {
          pendingRequests.push(() => resolve(httpClient(originalRequest)));
        });
      }

      isRefreshing = true;

      try {
        const response = await axios.post(`${BASE_URL}/auth/refresh`, {
          refreshToken,
        });

        const data = response.data;
        const refreshFailed =
          !data ||
          typeof data !== 'object' ||
          !data.accessToken ||
          !data.refreshToken;

        if (refreshFailed) {
          throw new Error('Refresh token is invalid or expired.');
        }

        useAuthStore.getState().setTokens(data.accessToken, data.refreshToken);

        pendingRequests.forEach((callback) => callback());
        pendingRequests = [];

        return httpClient(originalRequest);
      } catch (refreshError) {
        useAuthStore.getState().clearSession();
        pendingRequests = [];
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);