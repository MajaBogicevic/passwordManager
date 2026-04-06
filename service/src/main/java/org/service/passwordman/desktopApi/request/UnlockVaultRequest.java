package org.service.passwordman.desktopApi.request;

public class UnlockVaultRequest {
    private int userId;
    private String masterPassword;

    public UnlockVaultRequest() {
    }

    public UnlockVaultRequest(int userId, String masterPassword) {
        this.userId = userId;
        this.masterPassword = masterPassword;
    }

    public int getUserId() {
        return userId;
    }

    public String getMasterPassword() {
        return masterPassword;
    }
}