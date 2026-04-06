package org.service.passwordman.desktopApi.request;

public class ChangeLoginPasswordRequest {
    private int userId;
    private String oldLoginPassword;
    private String newLoginPassword;

    public ChangeLoginPasswordRequest() {
    }

    public ChangeLoginPasswordRequest(int userId, String oldLoginPassword, String newLoginPassword) {
        this.userId = userId;
        this.oldLoginPassword = oldLoginPassword;
        this.newLoginPassword = newLoginPassword;
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
}