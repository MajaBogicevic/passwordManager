export interface AuditLogEntry {
  id: number;
  userId: number;
  eventType: string;
  outcome: string;
  reasonCode: string | null;
  ipAddress: string | null;
  sessionId: string | null;
  details: string | null;
  timestamp: string;
}

export interface PagedAuditLogResponse {
  items: AuditLogEntry[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}