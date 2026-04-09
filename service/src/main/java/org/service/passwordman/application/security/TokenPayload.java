package org.service.passwordman.application.security;

public class TokenPayload {

    private final int userId;
    private final String username;
    private final String jwtTokenId;
    private final String sessionId;
    private final String tokenType;

    public TokenPayload(
            int userId,
            String username,
            String jwtTokenId,
            String sessionId,
            String tokenType
    ) {
        this.userId = userId;
        this.username = username;
        this.jwtTokenId = jwtTokenId;
        this.sessionId = sessionId;
        this.tokenType = tokenType;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getJwtTokenId() {
        return jwtTokenId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTokenType() {
        return tokenType;
    }
}