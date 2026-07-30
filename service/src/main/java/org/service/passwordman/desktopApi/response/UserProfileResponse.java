package org.service.passwordman.desktopApi.response;

import java.time.LocalDateTime;

public class UserProfileResponse {

    private final boolean success = true;
    private final String username;
    private final String email;
    private final LocalDateTime createdAt;

    public UserProfileResponse(String username, String email, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    public boolean isSuccess() { return success; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}