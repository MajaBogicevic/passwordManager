package org.service.passwordman.desktopApi.response;

public class CopyPasswordResponse {
    private int entryId;
    private String password;
    private boolean copied;
    private String message;

    public CopyPasswordResponse() {
    }

    public CopyPasswordResponse(int entryId, String password, boolean copied, String message) {
        this.entryId = entryId;
        this.password = password;
        this.copied = copied;
        this.message = message;
    }

    public int getEntryId() {
        return entryId;
    }

    public String getPassword() {
        return password;
    }

    public boolean isCopied() {
        return copied;
    }

    public String getMessage() {
        return message;
    }
}