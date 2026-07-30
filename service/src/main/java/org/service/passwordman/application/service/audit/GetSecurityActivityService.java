package org.service.passwordman.application.service.audit;

import java.time.LocalDateTime;
import java.util.List;

import org.service.passwordman.application.usecase.audit.GetSecurityActivityUseCase;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.AuditLog;
import org.service.passwordman.domain.repository.AuditLogRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class GetSecurityActivityService implements GetSecurityActivityUseCase {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public GetSecurityActivityService(
            AuditLogRepository auditLogRepository,
            UserRepository userRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AuditActivityPage execute(
            int userId,
            String eventTypeContains,
            String outcome,
            LocalDateTime fromTimestamp,
            LocalDateTime toTimestamp,
            int page,
            int size
    ) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (page < 0) {
            throw new ValidationException("Page must not be negative.");
        }

        if (size <= 0 || size > 100) {
            throw new ValidationException("Size must be between 1 and 100.");
        }

        String eventTypeFilter = eventTypeContains == null ? null : eventTypeContains.trim().toLowerCase();
        String outcomeFilter = outcome == null ? null : outcome.trim();

        List<AuditLog> filtered = auditLogRepository.findSecurityByUserId(userId).stream()
                .filter(log -> eventTypeFilter == null
                        || eventTypeFilter.isEmpty()
                        || log.getEventType().name().toLowerCase().contains(eventTypeFilter))
                .filter(log -> outcomeFilter == null
                        || outcomeFilter.isEmpty()
                        || log.getOutcome().name().equalsIgnoreCase(outcomeFilter))
                .filter(log -> fromTimestamp == null || !log.getTimestamp().isBefore(fromTimestamp))
                .filter(log -> toTimestamp == null || !log.getTimestamp().isAfter(toTimestamp))
                .toList();

        int totalElements = filtered.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<AuditLog> pageItems = filtered.subList(fromIndex, toIndex);

        return new AuditActivityPage(pageItems, totalElements);
    }
}