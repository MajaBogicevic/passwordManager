package org.service.passwordman.desktopApi.response;

public class FolderResponse {
    private int id;
    private int userId;
    private String name;

    public FolderResponse() {
    }

    public FolderResponse(int id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}