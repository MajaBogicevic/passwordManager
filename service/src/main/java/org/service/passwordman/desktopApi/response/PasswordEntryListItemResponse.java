package org.service.passwordman.desktopApi.response;

import java.time.LocalDateTime;

public class PasswordEntryListItemResponse {
    private int id;
    private int userId;
    private String title;
    private String url;
    private String username;
    private String notes;
    private int folderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PasswordEntryListItemResponse() {
    }

    public PasswordEntryListItemResponse(
            int id,
            int userId,
            String title,
            String url,
            String username,
            String notes,
            int folderId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.url = url;
        this.username = username;
        this.notes = notes;
        this.folderId = folderId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
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

    public String getNotes() {
        return notes;
    }

    public int getFolderId() {
        return folderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}