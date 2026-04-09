package org.service.passwordman.infrastructure.audit;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.security.SecurityAuditEvent;
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
    public void log(SecurityAuditEvent event) {
        AuditLog auditLog = new AuditLog(
                0,
                event.getUserId(),
                event.getEventType(),
                event.getOutcome(),
                event.getReasonCode(),
                event.getIpAddress(),
                event.getSessionId(),
                event.getDetails(),
                clock.now()
        );

        auditLogRepository.save(auditLog);
    }
}