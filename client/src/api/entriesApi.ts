import { httpClient } from './httpClient';
import type { ApiSuccessResponse } from '../types/auth';
import type {
  CreatePasswordEntryRequest,
  PasswordEntry,
  RevealPasswordResponse,
  UpdatePasswordEntryRequest,
} from '../types/entry';    

export const entriesApi = {
  getAll: () => httpClient.get<PasswordEntry[]>('/entries'),

  getByFolder: (folderId: number) =>
    httpClient.get<PasswordEntry[]>(`/folders/${folderId}/entries`),

  getById: (entryId: number) =>
    httpClient.get<PasswordEntry>(`/entries/${entryId}`),

  create: (data: CreatePasswordEntryRequest) =>
    httpClient.post<ApiSuccessResponse>('/entries', data),

  update: (data: UpdatePasswordEntryRequest) =>
    httpClient.put<ApiSuccessResponse>('/entries', data),

  delete: (entryId: number) =>
    httpClient.delete<ApiSuccessResponse>(`/entries/${entryId}`),

  reveal: (entryId: number) =>
    httpClient.post<RevealPasswordResponse>(`/entries/${entryId}/reveal`),

  search: (titleQuery: string) =>
    httpClient.post<PasswordEntry[]>('/entries/search', { titleQuery }),

  logCopy: (entryId: number) =>
    httpClient.post<ApiSuccessResponse>(`/entries/${entryId}/log-copy`, {}),
};