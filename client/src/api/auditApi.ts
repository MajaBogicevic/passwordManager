import { httpClient } from './httpClient';
import type { PagedAuditLogResponse } from '../types/audit';

export interface AuditActivityParams {
  eventType?: string;
  outcome?: string;
  fromDate?: string;
  toDate?: string;
  page?: number;
  size?: number;
}

export const auditApi = {
  getActivity: (params: AuditActivityParams) =>
    httpClient.get<PagedAuditLogResponse>('/audit/activity', { params }),
};