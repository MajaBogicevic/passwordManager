package org.service.passwordman.infrastructure.security;

public class AuthenticatedUser {

    private final int userId;
    private final String username;
    private final String jwtTokenId;

    public AuthenticatedUser(int userId, String username, String jwtTokenId) {
        this.userId = userId;
        this.username = username;
        this.jwtTokenId = jwtTokenId;
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
}