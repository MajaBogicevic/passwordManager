package org.service.passwordman.desktopApi.response;

public class AuthResponse {
    private boolean success;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}