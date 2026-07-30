package org.service.passwordman.application.usecase.audit;

import java.time.LocalDateTime;

import org.service.passwordman.application.service.audit.AuditActivityPage;

public interface GetSecurityActivityUseCase {
    AuditActivityPage execute(
            int userId,
            String eventTypeContains,
            String outcome,
            LocalDateTime fromTimestamp,
            LocalDateTime toTimestamp,
            int page,
            int size
    );
}