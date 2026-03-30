package org.service.passwordman.domain.repository;

import org.service.passwordman.domain.model.AuditLog;

import java.util.List;

public interface AuditLogRepository {
    void save(AuditLog auditLog);
    List<AuditLog> findByUserId(int userId);
}