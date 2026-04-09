package org.service.passwordman.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.service.passwordman.application.port.RefreshTokenRecord;
import org.service.passwordman.application.port.RefreshTokenStore;
import org.service.passwordman.infrastructure.persistence.entity.RefreshTokenEntity;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataRefreshTokenJpaRepository;

public class JpaRefreshTokenStoreAdapter implements RefreshTokenStore {

    private final SpringDataRefreshTokenJpaRepository repository;

    public JpaRefreshTokenStoreAdapter(SpringDataRefreshTokenJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(String refreshTokenId, int userId, String tokenFamilyId, long expiresAtMillis) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setTokenId(refreshTokenId);
        entity.setUserId(userId);
        entity.setTokenFamilyId(tokenFamilyId);
        entity.setExpiresAtMillis(expiresAtMillis);
        entity.setRevoked(false);
        entity.setConsumed(false);
        entity.setFamilyRevoked(false);
        repository.save(entity);
    }

    @Override
    public Optional<RefreshTokenRecord> findByTokenId(String refreshTokenId) {
        cleanupExpiredTokens();
        return repository.findById(refreshTokenId).map(this::toRecord);
    }

    @Override
    public void rotate(
            String currentRefreshTokenId,
            String newRefreshTokenId,
            int userId,
            String tokenFamilyId,
            long newExpiresAtMillis
    ) {
        cleanupExpiredTokens();

        RefreshTokenEntity current = repository.findById(currentRefreshTokenId)
                .orElseThrow(() -> new IllegalStateException("Current refresh token does not exist."));

        if (current.getUserId() != userId) {
            throw new IllegalStateException("Refresh token does not belong to the expected user.");
        }

        if (!current.getTokenFamilyId().equals(tokenFamilyId)) {
            throw new IllegalStateException("Refresh token family mismatch.");
        }

        current.setConsumed(true);
        repository.save(current);

        RefreshTokenEntity replacement = new RefreshTokenEntity();
        replacement.setTokenId(newRefreshTokenId);
        replacement.setUserId(userId);
        replacement.setTokenFamilyId(tokenFamilyId);
        replacement.setExpiresAtMillis(newExpiresAtMillis);
        replacement.setRevoked(false);
        replacement.setConsumed(false);
        replacement.setFamilyRevoked(current.isFamilyRevoked());

        repository.save(replacement);
    }

    @Override
    public void revoke(String refreshTokenId) {
        repository.findById(refreshTokenId).ifPresent(entity -> {
            entity.setRevoked(true);
            repository.save(entity);
        });
    }

    @Override
    public void revokeFamily(String tokenFamilyId) {
        List<RefreshTokenEntity> familyTokens = repository.findByTokenFamilyId(tokenFamilyId);
        for (RefreshTokenEntity token : familyTokens) {
            token.setRevoked(true);
            token.setFamilyRevoked(true);
        }
        repository.saveAll(familyTokens);
    }

    @Override
    public void revokeAllByUserId(int userId) {
        List<RefreshTokenEntity> userTokens = repository.findByUserId(userId);
        for (RefreshTokenEntity token : userTokens) {
            token.setRevoked(true);
            token.setFamilyRevoked(true);
        }
        repository.saveAll(userTokens);
    }

    private RefreshTokenRecord toRecord(RefreshTokenEntity entity) {
        return new RefreshTokenRecord(
                entity.getUserId(),
                entity.getTokenFamilyId(),
                entity.getExpiresAtMillis(),
                entity.isRevoked(),
                entity.isConsumed(),
                entity.isFamilyRevoked()
        );
    }

    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        repository.findAll().stream()
                .filter(token -> now >= token.getExpiresAtMillis())
                .forEach(token -> repository.deleteById(token.getTokenId()));
    }
}