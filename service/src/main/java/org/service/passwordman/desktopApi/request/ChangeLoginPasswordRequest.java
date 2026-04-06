package org.service.passwordman.desktopApi.request;

public class ChangeLoginPasswordRequest {
    private int userId;
    private String oldLoginPassword;
    private String newLoginPassword;
    private String ipAddress;

    public ChangeLoginPasswordRequest() {
    }

    public ChangeLoginPasswordRequest(int userId, String oldLoginPassword, String newLoginPassword, String ipAddress) {
        this.userId = userId;
        this.oldLoginPassword = oldLoginPassword;
        this.newLoginPassword = newLoginPassword;
        this.ipAddress = ipAddress;
    }

    public int getUserId() {
        return userId;
    }

    public String getOldLoginPassword() {
        return oldLoginPassword;
    }

    public String getNewLoginPassword() {
        return newLoginPassword;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}