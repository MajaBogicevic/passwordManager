package org.service.passwordman.desktopApi.request;

public class ChangeMasterPasswordRequest {
    private String oldMasterPassword;
    private String newMasterPassword;
    private String ipAddress;

    public ChangeMasterPasswordRequest() {
    }

    public ChangeMasterPasswordRequest(String oldMasterPassword, String newMasterPassword, String ipAddress) {
        this.oldMasterPassword = oldMasterPassword;
        this.newMasterPassword = newMasterPassword;
        this.ipAddress = ipAddress;
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