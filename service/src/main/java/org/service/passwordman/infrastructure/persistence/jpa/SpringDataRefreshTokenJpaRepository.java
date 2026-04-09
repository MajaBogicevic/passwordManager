package org.service.passwordman.infrastructure.persistence.jpa;

import java.util.List;

import org.service.passwordman.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, String> {
    List<RefreshTokenEntity> findByTokenFamilyId(String tokenFamilyId);
    List<RefreshTokenEntity> findByUserId(int userId);
}