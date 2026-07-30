import { httpClient } from './httpClient';
import type { UserProfile } from '../types/user';

export const usersApi = {
  getMe: () => httpClient.get<UserProfile>('/users/me'),
};