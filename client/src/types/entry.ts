export interface PasswordEntry {
  id: number;
  userId: number;
  title: string;
  url: string | null;
  username: string;
  notes: string | null;
  folderId: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePasswordEntryRequest {
  title: string;
  url: string | null;
  username: string;
  plainPassword: string;
  notes: string | null;
  folderId: number;
}

export interface UpdatePasswordEntryRequest {
  entryId: number;
  title: string;
  url: string | null;
  username: string;
  plainPassword: string;
  notes: string | null;
  folderId: number;
}

export interface RevealPasswordResponse {
  entryId: number;
  password: string;
}