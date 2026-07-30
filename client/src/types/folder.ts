export interface Folder {
  id: number;
  userId: number;
  name: string;
}

export interface CreateFolderRequest {
  folderName: string;
}

export interface CreateFolderResponse {
  success: boolean;
  message: string;
  folderId: number;
  name: string;
}

export interface RenameFolderRequest {
  folderId: number;
  newName: string;
}