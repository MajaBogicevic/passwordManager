package org.service.passwordman.desktopApi.request;

public class CreatePasswordEntryRequest {
    private int userId;
    private String title;
    private String url;
    private String username;
    private String plainPassword;
    private String notes;
    private int folderId;
    private String ipAddress;

    public CreatePasswordEntryRequest() {
    }

    public CreatePasswordEntryRequest(
            String title,
            String url,
            String username,
            String plainPassword,
            String notes,
            int folderId,
            String ipAddress
    ) {
        this.title = title;
        this.url = url;
        this.username = username;
        this.plainPassword = plainPassword;
        this.notes = notes;
        this.folderId = folderId;
        this.ipAddress = ipAddress;
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

    public String getIpAddress() {
        return ipAddress;
    }
}