package org.service.passwordman.infrastructure.security;

public class AuthenticatedUser {

    private final int userId;
    private final String username;
    private final String sessionId;
    private final String jwtTokenId;
    private final long accessTokenExpiresAtMillis;

    public AuthenticatedUser(
            int userId,
            String username,
            String sessionId,
            String jwtTokenId,
            long accessTokenExpiresAtMillis
    ) {
        this.userId = userId;
        this.username = username;
        this.sessionId = sessionId;
        this.jwtTokenId = jwtTokenId;
        this.accessTokenExpiresAtMillis = accessTokenExpiresAtMillis;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getJwtTokenId() {
        return jwtTokenId;
    }

    public long getAccessTokenExpiresAtMillis() {
        return accessTokenExpiresAtMillis;
    }
}