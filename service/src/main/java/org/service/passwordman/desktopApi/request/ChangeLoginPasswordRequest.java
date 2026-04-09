package org.service.passwordman.desktopApi.request;

public class ChangeLoginPasswordRequest {
    private String oldLoginPassword;
    private String newLoginPassword;

    public ChangeLoginPasswordRequest() {
    }

    public ChangeLoginPasswordRequest(String oldLoginPassword, String newLoginPassword, String ipAddress) {
        this.oldLoginPassword = oldLoginPassword;
        this.newLoginPassword = newLoginPassword;
    }

    public String getOldLoginPassword() {
        return oldLoginPassword;
    }

    public String getNewLoginPassword() {
        return newLoginPassword;
    }
}