package org.service.passwordman.infrastructure.persistence.jpa;

import java.util.List;

import org.service.passwordman.infrastructure.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAuditLogJpaRepository extends JpaRepository<AuditLogEntity, Integer> {
    List<AuditLogEntity> findByUserIdOrderByTimestampDesc(int userId);
}