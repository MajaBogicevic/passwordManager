package org.service.passwordman.desktopApi.handler;

import org.service.passwordman.application.usecase.audit.GetSecurityActivityUseCase;
import org.service.passwordman.desktopApi.response.AuditLogResponse;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.AuditLog;

import java.util.List;
import java.util.stream.Collectors;

public class AuditHandler {

    private final GetSecurityActivityUseCase getSecurityActivityUseCase;
    private final ApiHandler apiHandler;

    public AuditHandler(
            GetSecurityActivityUseCase getSecurityActivityUseCase,
            ApiHandler apiHandler
    ) {
        this.getSecurityActivityUseCase = getSecurityActivityUseCase;
        this.apiHandler = apiHandler;
    }

    public List<AuditLogResponse> getSecurityActivity(int userId) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        List<AuditLog> logs = getSecurityActivityUseCase.execute(userId);

        return logs.stream()
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getIp(),
                        log.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    public Object getSecurityActivitySafe(int userId) {
        return apiHandler.execute(() -> getSecurityActivity(userId));
    }
}