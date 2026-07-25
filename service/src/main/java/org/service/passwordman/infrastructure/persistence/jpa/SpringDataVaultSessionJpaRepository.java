package org.service.passwordman.infrastructure.persistence.jpa;

import java.util.List;

import org.service.passwordman.infrastructure.persistence.entity.VaultSessionEntity;
import org.service.passwordman.infrastructure.persistence.entity.VaultSessionEntity.VaultSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface SpringDataVaultSessionJpaRepository extends JpaRepository<VaultSessionEntity, VaultSessionId> {
    List<VaultSessionEntity> findByUserId(int userId);

    @Modifying
    @Transactional
    void deleteByUserIdAndJwtTokenId(int userId, String jwtTokenId);

    long countByUserIdAndJwtTokenId(int userId, String jwtTokenId);
}