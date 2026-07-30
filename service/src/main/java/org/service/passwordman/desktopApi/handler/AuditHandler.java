package org.service.passwordman.desktopApi.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.service.passwordman.application.service.audit.AuditActivityPage;
import org.service.passwordman.application.usecase.audit.GetSecurityActivityUseCase;
import org.service.passwordman.desktopApi.response.AuditLogResponse;
import org.service.passwordman.desktopApi.response.PagedAuditLogResponse;
import org.service.passwordman.domain.exception.ValidationException;
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

    public PagedAuditLogResponse getSecurityActivity(
            String eventType,
            String outcome,
            String fromDate,
            String toDate,
            int page,
            int size
    ) {
        int currentUserId = currentUserProvider.requireUserId();

        LocalDateTime from = parseFrom(fromDate);
        LocalDateTime to = parseTo(toDate);

        AuditActivityPage result = getSecurityActivityUseCase.execute(
                currentUserId,
                eventType,
                outcome,
                from,
                to,
                page,
                size
        );

        List<AuditLogResponse> items = result.getItems().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PagedAuditLogResponse(items, page, size, result.getTotalElements());
    }

    public Object getSecurityActivitySafe(
            String eventType,
            String outcome,
            String fromDate,
            String toDate,
            int page,
            int size
    ) {
        return apiHandler.execute(() -> getSecurityActivity(eventType, outcome, fromDate, toDate, page, size));
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getUserId(),
                log.getEventType().name(),
                log.getOutcome().name(),
                log.getReasonCode(),
                log.getIpAddress(),
                log.getSessionId(),
                log.getDetails(),
                log.getTimestamp()
        );
    }

    private LocalDateTime parseFrom(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr).atStartOfDay();
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Invalid 'fromDate' format. Expected yyyy-MM-dd.");
        }
    }

    private LocalDateTime parseTo(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr).atTime(LocalTime.MAX);
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Invalid 'toDate' format. Expected yyyy-MM-dd.");
        }
    }
}