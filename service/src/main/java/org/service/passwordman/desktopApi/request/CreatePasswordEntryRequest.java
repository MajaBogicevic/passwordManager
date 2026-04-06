package org.service.passwordman.desktopApi.request;

public class CreatePasswordEntryRequest {
    private int userId;
    private String title;
    private String url;
    private String username;
    private String plainPassword;
    private String notes;
    private int folderId;

    public CreatePasswordEntryRequest() {
    }

    public CreatePasswordEntryRequest(
            int userId,
            String title,
            String url,
            String username,
            String plainPassword,
            String notes,
            int folderId
    ) {
        this.userId = userId;
        this.title = title;
        this.url = url;
        this.username = username;
        this.plainPassword = plainPassword;
        this.notes = notes;
        this.folderId = folderId;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public String getNotes() {
        return notes;
    }

    public int getFolderId() {
        return folderId;
    }
}