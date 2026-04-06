package org.service.passwordman.desktopApi.request;

public class LoginRequest {
    private String username;
    private String loginPassword;
    private String ipAddress;

    public LoginRequest() {
    }

    public LoginRequest(String username, String loginPassword, String ipAddress) {
        this.username = username;
        this.loginPassword = loginPassword;
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    
}