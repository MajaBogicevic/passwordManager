import { httpClient } from './httpClient';
import type { ApiSuccessResponse } from '../types/auth';
import type { CreateFolderRequest, CreateFolderResponse, Folder } from '../types/folder';

export const folderApi = {
  getAll: () => httpClient.get<Folder[]>('/folders'),

  getById: (folderId: number) => httpClient.get<Folder>(`/folders/${folderId}`),

  create: (data: CreateFolderRequest) =>
    httpClient.post<CreateFolderResponse>('/folders', data),

  rename: (data: { folderId: number; newName: string }) =>
    httpClient.put<ApiSuccessResponse>('/folders', data),

  delete: (folderId: number) =>
    httpClient.delete<ApiSuccessResponse>(`/folders/${folderId}`),
};