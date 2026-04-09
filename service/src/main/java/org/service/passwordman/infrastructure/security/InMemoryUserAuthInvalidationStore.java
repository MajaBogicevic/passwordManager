package org.service.passwordman.infrastructure.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.service.passwordman.application.port.UserAuthInvalidationStore;

public class InMemoryUserAuthInvalidationStore implements UserAuthInvalidationStore {

    private final Map<Integer, Long> validAfterByUserId = new ConcurrentHashMap<>();

    @Override
    public void invalidateAllTokensForUser(int userId, long validAfterEpochMillis) {
        validAfterByUserId.put(userId, validAfterEpochMillis);
    }

    @Override
    public long getTokensValidAfterForUser(int userId) {
        return validAfterByUserId.getOrDefault(userId, 0L);
    }
}