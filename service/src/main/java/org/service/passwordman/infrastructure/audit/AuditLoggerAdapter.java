package org.service.passwordman.infrastructure.audit;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.domain.model.AuditLog;
import org.service.passwordman.domain.repository.AuditLogRepository;

public class AuditLoggerAdapter implements AuditLogger {

    private final AuditLogRepository auditLogRepository;
    private final Clock clock;

    public AuditLoggerAdapter(AuditLogRepository auditLogRepository, Clock clock) {
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
    }

    @Override
    public void log(int userId, String action, String ip) {
        AuditLog auditLog = new AuditLog(
                0,
                userId,
                action,
                ip,
                clock.now()
        );

        auditLogRepository.save(auditLog);
    }
}