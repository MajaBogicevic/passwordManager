package org.service.passwordman.infrastructure.persistence.adapter;

import java.util.List;

import org.service.passwordman.domain.model.AuditLog;
import org.service.passwordman.domain.model.SecurityEventOutcome;
import org.service.passwordman.domain.model.SecurityEventType;
import org.service.passwordman.domain.repository.AuditLogRepository;
import org.service.passwordman.infrastructure.persistence.entity.AuditLogEntity;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataAuditLogJpaRepository;

public class JpaAuditLogRepositoryAdapter implements AuditLogRepository {

    private final SpringDataAuditLogJpaRepository repository;

    public JpaAuditLogRepositoryAdapter(SpringDataAuditLogJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(AuditLog auditLog) {
        repository.save(toEntity(auditLog));
    }

    @Override
    public List<AuditLog> findSecurityByUserId(int userId) {
        return repository.findByUserIdOrderByTimestampDesc(userId).stream()
                .map(this::toDomain)
                .filter(log -> log.getEventType().isSecurityEvent())
                .toList();
    }

    private AuditLog toDomain(AuditLogEntity entity) {
        return new AuditLog(
                entity.getId(),
                entity.getUserId(),
                SecurityEventType.valueOf(entity.getEventType()),
                SecurityEventOutcome.valueOf(entity.getOutcome()),
                entity.getReasonCode(),
                entity.getIpAddress(),
                entity.getSessionId(),
                entity.getDetails(),
                entity.getTimestamp()
        );
    }

    private AuditLogEntity toEntity(AuditLog auditLog) {
        AuditLogEntity entity = new AuditLogEntity();
        if (auditLog.getId() > 0) {
            entity.setId(auditLog.getId());
        }
        entity.setUserId(auditLog.getUserId());
        entity.setEventType(auditLog.getEventType().name());
        entity.setOutcome(auditLog.getOutcome().name());
        entity.setReasonCode(auditLog.getReasonCode());
        entity.setIpAddress(auditLog.getIpAddress());
        entity.setSessionId(auditLog.getSessionId());
        entity.setDetails(auditLog.getDetails());
        entity.setTimestamp(auditLog.getTimestamp());
        return entity;
    }
}