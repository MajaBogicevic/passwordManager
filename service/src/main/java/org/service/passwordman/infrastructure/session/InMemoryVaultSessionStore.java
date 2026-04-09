package org.service.passwordman.infrastructure.session;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.service.passwordman.application.port.VaultSessionStore;

public class InMemoryVaultSessionStore implements VaultSessionStore {

    private static class VaultSessionRecord {
        private final int userId;
        @SuppressWarnings("unused")
        private final String jwtTokenId;
        private LocalDateTime lastActivityAt;

        private VaultSessionRecord(int userId, String jwtTokenId, LocalDateTime lastActivityAt) {
            this.userId = userId;
            this.jwtTokenId = jwtTokenId;
            this.lastActivityAt = lastActivityAt;
        }
    }

    private final ConcurrentMap<String, VaultSessionRecord> activeVaultSessions = new ConcurrentHashMap<>();

    @Override
    public void unlock(int userId, String jwtTokenId, LocalDateTime unlockedAt) {
        activeVaultSessions.put(
                toSessionKey(userId, jwtTokenId),
                new VaultSessionRecord(userId, jwtTokenId, unlockedAt)
        );
    }

    @Override
    public void lock(int userId, String jwtTokenId) {
        activeVaultSessions.remove(toSessionKey(userId, jwtTokenId));
    }

    @Override
    public void lockAllForUser(int userId) {
        Iterator<Map.Entry<String, VaultSessionRecord>> iterator = activeVaultSessions.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, VaultSessionRecord> entry = iterator.next();
            if (entry.getValue().userId == userId) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean isUnlocked(int userId, String jwtTokenId) {
        return activeVaultSessions.containsKey(toSessionKey(userId, jwtTokenId));
    }

    @Override
    public void refreshActivity(int userId, String jwtTokenId, LocalDateTime activityAt) {
        String sessionKey = toSessionKey(userId, jwtTokenId);
        VaultSessionRecord record = activeVaultSessions.get(sessionKey);

        if (record != null) {
            record.lastActivityAt = activityAt;
        }
    }

    @Override
    public Optional<LocalDateTime> getLastActivityAt(int userId, String jwtTokenId) {
        VaultSessionRecord record = activeVaultSessions.get(toSessionKey(userId, jwtTokenId));
        return record == null ? Optional.empty() : Optional.of(record.lastActivityAt);
    }

    private String toSessionKey(int userId, String jwtTokenId) {
        if (jwtTokenId == null || jwtTokenId.isBlank()) {
            throw new IllegalArgumentException("JWT token id is required.");
        }
        return userId + ":" + jwtTokenId;
    }
}