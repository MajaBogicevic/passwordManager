package org.service.passwordman.desktopApi.request;

public class ChangeMasterPasswordRequest {
    private int userId;
    private String oldMasterPassword;
    private String newMasterPassword;

    public ChangeMasterPasswordRequest() {
    }

    public ChangeMasterPasswordRequest(int userId, String oldMasterPassword, String newMasterPassword) {
        this.userId = userId;
        this.oldMasterPassword = oldMasterPassword;
        this.newMasterPassword = newMasterPassword;
    }

    public int getUserId() {
        return userId;
    }

    public String getOldMasterPassword() {
        return oldMasterPassword;
    }

    public String getNewMasterPassword() {
        return newMasterPassword;
    }
}