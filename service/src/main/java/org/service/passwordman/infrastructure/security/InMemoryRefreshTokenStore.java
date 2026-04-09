package org.service.passwordman.infrastructure.security;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.service.passwordman.application.port.RefreshTokenRecord;
import org.service.passwordman.application.port.RefreshTokenStore;

public class InMemoryRefreshTokenStore implements RefreshTokenStore {

    private static class StoredRefreshToken {
        private final int userId;
        private final String tokenFamilyId;
        private final long expiresAtMillis;
        private volatile boolean revoked;
        private volatile boolean consumed;

        private StoredRefreshToken(
                int userId,
                String tokenFamilyId,
                long expiresAtMillis,
                boolean revoked,
                boolean consumed
        ) {
            this.userId = userId;
            this.tokenFamilyId = tokenFamilyId;
            this.expiresAtMillis = expiresAtMillis;
            this.revoked = revoked;
            this.consumed = consumed;
        }
    }

    private final Map<String, StoredRefreshToken> tokens = new ConcurrentHashMap<>();
    private final Set<String> revokedFamilies = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized void save(String refreshTokenId, int userId, String tokenFamilyId, long expiresAtMillis) {
        cleanupExpiredTokens();

        tokens.put(
                refreshTokenId,
                new StoredRefreshToken(
                        userId,
                        tokenFamilyId,
                        expiresAtMillis,
                        false,
                        false
                )
        );
    }

    @Override
    public synchronized Optional<RefreshTokenRecord> findByTokenId(String refreshTokenId) {
        cleanupExpiredTokens();

        StoredRefreshToken stored = tokens.get(refreshTokenId);
        if (stored == null) {
            return Optional.empty();
        }

        return Optional.of(toRecord(stored));
    }

    @Override
    public synchronized void rotate(
            String currentRefreshTokenId,
            String newRefreshTokenId,
            int userId,
            String tokenFamilyId,
            long newExpiresAtMillis
    ) {
        cleanupExpiredTokens();

        StoredRefreshToken current = tokens.get(currentRefreshTokenId);

        if (current == null) {
            throw new IllegalStateException("Current refresh token does not exist.");
        }

        if (current.userId != userId) {
            throw new IllegalStateException("Refresh token does not belong to the expected user.");
        }

        if (!current.tokenFamilyId.equals(tokenFamilyId)) {
            throw new IllegalStateException("Refresh token family mismatch.");
        }

        current.consumed = true;

        tokens.put(
                newRefreshTokenId,
                new StoredRefreshToken(
                        userId,
                        tokenFamilyId,
                        newExpiresAtMillis,
                        false,
                        false
                )
        );
    }

    @Override
    public synchronized void revoke(String refreshTokenId) {
        StoredRefreshToken stored = tokens.get(refreshTokenId);
        if (stored != null) {
            stored.revoked = true;
        }
    }

    @Override
    public synchronized void revokeFamily(String tokenFamilyId) {
        revokedFamilies.add(tokenFamilyId);

        for (StoredRefreshToken stored : tokens.values()) {
            if (stored.tokenFamilyId.equals(tokenFamilyId)) {
                stored.revoked = true;
            }
        }
    }

    @Override
    public synchronized void revokeAllByUserId(int userId) {
        for (StoredRefreshToken stored : tokens.values()) {
            if (stored.userId == userId) {
                revokedFamilies.add(stored.tokenFamilyId);
                stored.revoked = true;
            }
        }
    }

    private RefreshTokenRecord toRecord(StoredRefreshToken stored) {
        return new RefreshTokenRecord(
                stored.userId,
                stored.tokenFamilyId,
                stored.expiresAtMillis,
                stored.revoked,
                stored.consumed,
                revokedFamilies.contains(stored.tokenFamilyId)
        );
    }

    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();

        tokens.entrySet().removeIf(entry -> now >= entry.getValue().expiresAtMillis);
    }
}