package org.service.passwordman.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.Optional;

import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.infrastructure.persistence.entity.VaultSessionEntity;
import org.service.passwordman.infrastructure.persistence.entity.VaultSessionEntity.VaultSessionId;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataVaultSessionJpaRepository;

public class JpaVaultSessionStoreAdapter implements VaultSessionStore {

    private final SpringDataVaultSessionJpaRepository repository;

    public JpaVaultSessionStoreAdapter(SpringDataVaultSessionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void unlock(int userId, String jwtTokenId, LocalDateTime unlockedAt) {
        validateJwtTokenId(jwtTokenId);

        VaultSessionEntity entity = new VaultSessionEntity();
        entity.setUserId(userId);
        entity.setJwtTokenId(jwtTokenId);
        entity.setLastActivityAt(unlockedAt);

        repository.save(entity);
    }

    @Override
    public void lock(int userId, String jwtTokenId) {
        validateJwtTokenId(jwtTokenId);
        repository.deleteByUserIdAndJwtTokenId(userId, jwtTokenId);
    }

    @Override
    public void lockAllForUser(int userId) {
        repository.deleteAll(repository.findByUserId(userId));
    }

    @Override
    public boolean isUnlocked(int userId, String jwtTokenId) {
        validateJwtTokenId(jwtTokenId);
        return repository.countByUserIdAndJwtTokenId(userId, jwtTokenId) > 0;
    }

    @Override
    public void refreshActivity(int userId, String jwtTokenId, LocalDateTime activityAt) {
        validateJwtTokenId(jwtTokenId);

        repository.findById(new VaultSessionId(userId, jwtTokenId)).ifPresent(entity -> {
            entity.setLastActivityAt(activityAt);
            repository.save(entity);
        });
    }

    @Override
    public Optional<LocalDateTime> getLastActivityAt(int userId, String jwtTokenId) {
        validateJwtTokenId(jwtTokenId);
        return repository.findById(new VaultSessionId(userId, jwtTokenId))
                .map(VaultSessionEntity::getLastActivityAt);
    }

    private void validateJwtTokenId(String jwtTokenId) {
        if (jwtTokenId == null || jwtTokenId.isBlank()) {
            throw new IllegalArgumentException("JWT token id is required.");
        }
    }
}