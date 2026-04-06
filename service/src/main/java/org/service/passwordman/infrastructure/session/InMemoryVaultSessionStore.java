package org.service.passwordman.infrastructure.session;

import org.service.passwordman.application.port.VaultSessionStore;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryVaultSessionStore implements VaultSessionStore {

    private final Map<Integer, LocalDateTime> activeVaultSessions = new ConcurrentHashMap<>();

    @Override
    public void unlock(int userId, LocalDateTime unlockedAt) {
        activeVaultSessions.put(userId, unlockedAt);
    }

    @Override
    public void lock(int userId) {
        activeVaultSessions.remove(userId);
    }

    @Override
    public boolean isUnlocked(int userId) {
        return activeVaultSessions.containsKey(userId);
    }

    @Override
    public void refreshActivity(int userId, LocalDateTime activityAt) {
        if (isUnlocked(userId)) {
            activeVaultSessions.put(userId, activityAt);
        }
    }

    @Override
    public Optional<LocalDateTime> getLastActivityAt(int userId) {
        return Optional.ofNullable(activeVaultSessions.get(userId));
    }
}