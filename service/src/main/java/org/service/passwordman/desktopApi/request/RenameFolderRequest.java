package org.service.passwordman.desktopApi.request;

public class RenameFolderRequest {
    private int folderId;
    private String newName;

    public RenameFolderRequest() {
    }

    public RenameFolderRequest(int folderId, String newName) {
        this.folderId = folderId;
        this.newName = newName;
    }

    public int getFolderId() {
        return folderId;
    }

    public String getNewName() {
        return newName;
    }
}