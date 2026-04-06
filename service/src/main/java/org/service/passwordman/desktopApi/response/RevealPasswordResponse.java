package org.service.passwordman.desktopApi.response;

public class RevealPasswordResponse {
    private int entryId;
    private String password;

    public RevealPasswordResponse() {
    }

    public RevealPasswordResponse(int entryId, String password) {
        this.entryId = entryId;
        this.password = password;
    }

    public int getEntryId() {
        return entryId;
    }

    public String getPassword() {
        return password;
    }
}