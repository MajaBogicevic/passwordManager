package org.service.passwordman.desktopApi.request;

public class LoginRequest {
    private String username;
    private String loginPassword;

    public LoginRequest() {
    }

    public LoginRequest(String username, String loginPassword) {
        this.username = username;
        this.loginPassword = loginPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getLoginPassword() {
        return loginPassword;
    }
}