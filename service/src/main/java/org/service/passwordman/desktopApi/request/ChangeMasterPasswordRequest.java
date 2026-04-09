package org.service.passwordman.desktopApi.request;

public class ChangeMasterPasswordRequest {
    private String oldMasterPassword;
    private String newMasterPassword;

    public ChangeMasterPasswordRequest() {
    }

    public ChangeMasterPasswordRequest(String oldMasterPassword, String newMasterPassword, String ipAddress) {
        this.oldMasterPassword = oldMasterPassword;
        this.newMasterPassword = newMasterPassword;
    }

    public String getOldMasterPassword() {
        return oldMasterPassword;
    }

    public String getNewMasterPassword() {
        return newMasterPassword;
    }

}