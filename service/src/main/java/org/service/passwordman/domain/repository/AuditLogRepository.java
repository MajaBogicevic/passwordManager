package org.service.passwordman.domain.repository;

import java.util.List;

import org.service.passwordman.domain.model.AuditLog;

public interface AuditLogRepository {

    void save(AuditLog auditLog);

    List<AuditLog> findSecurityByUserId(int userId);
}