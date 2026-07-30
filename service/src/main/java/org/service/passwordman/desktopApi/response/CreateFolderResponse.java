package org.service.passwordman.desktopApi.response;

public class CreateFolderResponse {

    private final boolean success = true;
    private final String message;
    private final int folderId;
    private final String name;

    public CreateFolderResponse(String message, int folderId, String name) {
        this.message = message;
        this.folderId = folderId;
        this.name = name;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getFolderId() {
        return folderId;
    }

    public String getName() {
        return name;
    }
}