package org.service.passwordman.application.port;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VaultSessionStore {
    void unlock(int userId, LocalDateTime unlockedAt);
    void lock(int userId);
    boolean isUnlocked(int userId);
    void refreshActivity(int userId, LocalDateTime activityAt);
    Optional<LocalDateTime> getLastActivityAt(int userId);
}