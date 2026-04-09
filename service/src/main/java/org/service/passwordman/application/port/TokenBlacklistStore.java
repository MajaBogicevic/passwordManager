package org.service.passwordman.application.port;

public interface TokenBlacklistStore {

    void blacklist(String jwtTokenId, long expiresAtMillis);

    boolean isBlacklisted(String jwtTokenId);
}