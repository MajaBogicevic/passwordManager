package org.service.passwordman.desktopApi.request;

public class CreateFolderRequest {
    private String folderName;

    public CreateFolderRequest() {
    }

    public CreateFolderRequest(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}