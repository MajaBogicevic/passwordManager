package org.service.passwordman.infrastructure.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.service.passwordman.application.port.TokenBlacklistStore;

public class InMemoryTokenBlacklistStore implements TokenBlacklistStore {

    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    @Override
    public void blacklist(String jwtTokenId, long expiresAtMillis) {
        blacklistedTokens.put(jwtTokenId, expiresAtMillis);
    }

    @Override
    public boolean isBlacklisted(String jwtTokenId) {
        Long expiresAt = blacklistedTokens.get(jwtTokenId);

        if (expiresAt == null) {
            return false;
        }

        long now = System.currentTimeMillis();

        if (now >= expiresAt) {
            blacklistedTokens.remove(jwtTokenId);
            return false;
        }

        return true;
    }
}