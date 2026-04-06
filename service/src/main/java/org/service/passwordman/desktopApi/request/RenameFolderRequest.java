package org.service.passwordman.desktopApi.request;

public class RenameFolderRequest {
    private int userId;
    private int folderId;
    private String newName;

    public RenameFolderRequest() {
    }

    public RenameFolderRequest(int userId, int folderId, String newName) {
        this.userId = userId;
        this.folderId = folderId;
        this.newName = newName;
    }

    public int getUserId() {
        return userId;
    }

    public int getFolderId() {
        return folderId;
    }

    public String getNewName() {
        return newName;
    }
}