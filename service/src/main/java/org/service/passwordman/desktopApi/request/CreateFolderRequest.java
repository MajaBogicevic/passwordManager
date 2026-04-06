package org.service.passwordman.desktopApi.request;

public class CreateFolderRequest {
    private int userId;
    private String folderName;

    public CreateFolderRequest() {
    }

    public CreateFolderRequest(int userId, String folderName) {
        this.userId = userId;
        this.folderName = folderName;
    }

    public int getUserId() {
        return userId;
    }

    public String getFolderName() {
        return folderName;
    }
}