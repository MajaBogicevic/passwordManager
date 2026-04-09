package org.service.passwordman.desktopApi.response;

public class RefreshTokenResponse {

    private final boolean success;
    private final String message;
    private final String accessToken;
    private final String refreshToken;

    public RefreshTokenResponse(String message, String accessToken, String refreshToken) {
        this.success = true;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
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

    public String getRefreshToken() {
        return refreshToken;
    }
}