package org.service.passwordman.desktopApi.response;

public class LoginResponse {

    private final boolean success;
    private final String message;
    private final String accessToken;

    public LoginResponse(String message, String accessToken) {
        this.success = true;
        this.message = message;
        this.accessToken = accessToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getAccessToken() {
        return accessToken;
    }
}