package org.service.passwordman.infrastructure.persistence.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.service.passwordman.domain.model.AuditLog;
import org.service.passwordman.domain.repository.AuditLogRepository;

public class InMemoryAuditLogRepository implements AuditLogRepository {

    private final Map<Integer, AuditLog> logsById = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public void save(AuditLog auditLog) {
        AuditLog logToStore = auditLog;

        if (auditLog.getId() == 0) {
            int newId = idGenerator.getAndIncrement();
            logToStore = new AuditLog(
                    newId,
                    auditLog.getUserId(),
                    auditLog.getEventType(),
                    auditLog.getOutcome(),
                    auditLog.getReasonCode(),
                    auditLog.getIpAddress(),
                    auditLog.getSessionId(),
                    auditLog.getDetails(),
                    auditLog.getTimestamp()
            );
        }

        logsById.put(logToStore.getId(), logToStore);
    }

    @Override
    public List<AuditLog> findSecurityByUserId(int userId) {
        return logsById.values()
                .stream()
                .filter(log -> log.getUserId() == userId)
                .filter(log -> log.getEventType().isSecurityEvent())
                .sorted(Comparator.comparing(AuditLog::getTimestamp).reversed())
                .toList();
    }
}