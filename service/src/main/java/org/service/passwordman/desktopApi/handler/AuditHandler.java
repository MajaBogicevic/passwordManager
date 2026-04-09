package org.service.passwordman.desktopApi.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.service.passwordman.application.usecase.audit.GetSecurityActivityUseCase;
import org.service.passwordman.desktopApi.response.AuditLogResponse;
import org.service.passwordman.domain.model.AuditLog;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;

public class AuditHandler {

    private final GetSecurityActivityUseCase getSecurityActivityUseCase;
    private final ApiHandler apiHandler;
    private final CurrentUserProvider currentUserProvider;

    public AuditHandler(
            GetSecurityActivityUseCase getSecurityActivityUseCase,
            ApiHandler apiHandler,
            CurrentUserProvider currentUserProvider
    ) {
        this.getSecurityActivityUseCase = getSecurityActivityUseCase;
        this.apiHandler = apiHandler;
        this.currentUserProvider = currentUserProvider;
    }

    public List<AuditLogResponse> getSecurityActivity() {
        int currentUserId = currentUserProvider.requireUserId();

        List<AuditLog> logs = getSecurityActivityUseCase.execute(currentUserId);

        return logs.stream()
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getEventType().name(),
                        log.getOutcome().name(),
                        log.getReasonCode(),
                        log.getIpAddress(),
                        log.getSessionId(),
                        log.getDetails(),
                        log.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    public Object getSecurityActivitySafe() {
        return apiHandler.execute(this::getSecurityActivity);
    }
}