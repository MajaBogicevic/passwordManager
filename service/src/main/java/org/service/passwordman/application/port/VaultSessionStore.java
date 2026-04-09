package org.service.passwordman.application.port;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VaultSessionStore {

    void unlock(int userId, String jwtTokenId, LocalDateTime unlockedAt);

    void lock(int userId, String jwtTokenId);

    void lockAllForUser(int userId);

    boolean isUnlocked(int userId, String jwtTokenId);

    void refreshActivity(int userId, String jwtTokenId, LocalDateTime activityAt);

    Optional<LocalDateTime> getLastActivityAt(int userId, String jwtTokenId);
}