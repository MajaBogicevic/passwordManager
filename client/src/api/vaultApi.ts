import { httpClient } from './httpClient';
import type { ApiSuccessResponse, UnlockVaultRequest } from '../types/auth';

export const vaultApi = {
  unlock: (data: UnlockVaultRequest) =>
    httpClient.post<ApiSuccessResponse>('/vault/unlock', data),

  lock: () => httpClient.post<ApiSuccessResponse>('/vault/lock', {}),

  autoLock: () => httpClient.post<ApiSuccessResponse>('/vault/auto-lock', {}),
};