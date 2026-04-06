package org.service.passwordman.desktopApi.request;

public class UnlockVaultRequest {
    private String masterPassword;

    public UnlockVaultRequest() {
    }

    public UnlockVaultRequest(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public String getMasterPassword() {
        return masterPassword;
    }
}