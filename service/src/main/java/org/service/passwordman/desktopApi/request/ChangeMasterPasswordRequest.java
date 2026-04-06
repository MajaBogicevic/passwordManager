package org.service.passwordman.desktopApi.request;

public class ChangeMasterPasswordRequest {
    private int userId;
    private String oldMasterPassword;
    private String newMasterPassword;
    private String ipAddress;

    public ChangeMasterPasswordRequest() {
    }

    public ChangeMasterPasswordRequest(int userId, String oldMasterPassword, String newMasterPassword, String ipAddress) {
        this.userId = userId;
        this.oldMasterPassword = oldMasterPassword;
        this.newMasterPassword = newMasterPassword;
        this.ipAddress = ipAddress;
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

    public String getIpAddress() {
        return ipAddress;
    }
}