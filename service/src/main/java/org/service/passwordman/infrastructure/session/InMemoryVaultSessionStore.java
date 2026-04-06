package org.service.passwordman.infrastructure.session;

import org.service.passwordman.application.port.VaultSessionStore;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryVaultSessionStore implements VaultSessionStore {

    private final ConcurrentMap<String, LocalDateTime> activeVaultSessions = new ConcurrentHashMap<>();

    @Override
    public void unlock(int userId, String jwtTokenId, LocalDateTime unlockedAt) {
        activeVaultSessions.put(toSessionKey(userId, jwtTokenId), unlockedAt);
    }

    @Override
    public void lock(int userId, String jwtTokenId) {
        activeVaultSessions.remove(toSessionKey(userId, jwtTokenId));
    }

    @Override
    public boolean isUnlocked(int userId, String jwtTokenId) {
        return activeVaultSessions.containsKey(toSessionKey(userId, jwtTokenId));
    }

    @Override
    public void refreshActivity(int userId, String jwtTokenId, LocalDateTime activityAt) {
        String sessionKey = toSessionKey(userId, jwtTokenId);
        if (activeVaultSessions.containsKey(sessionKey)) {
            activeVaultSessions.put(sessionKey, activityAt);
        }
    }

    @Override
    public Optional<LocalDateTime> getLastActivityAt(int userId, String jwtTokenId) {
        return Optional.ofNullable(activeVaultSessions.get(toSessionKey(userId, jwtTokenId)));
    }

    private String toSessionKey(int userId, String jwtTokenId) {
        if (jwtTokenId == null || jwtTokenId.isBlank()) {
            throw new IllegalArgumentException("JWT token id is required.");
        }
        return userId + ":" + jwtTokenId;
    }
}