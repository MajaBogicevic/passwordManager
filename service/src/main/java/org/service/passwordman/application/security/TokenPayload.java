package org.service.passwordman.application.security;

public class TokenPayload {

    private final int userId;
    private final String username;

    public TokenPayload(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}