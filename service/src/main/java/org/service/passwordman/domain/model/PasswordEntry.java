package org.service.passwordman.domain.model;

import java.util.Objects;
import java.time.LocalDateTime;

public class PasswordEntry {
    private int id;
    private int userId;
    private String title;
    private String username;
    private String encryptedPassword;
    private String url;
    private String notes;
    private int folderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PasswordEntry(int id, int userId, String title, String username, String encryptedPassword, String url, int folderId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.url = url;
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

    public String getUsername() {
        return username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public String getUrl() {
        return url;
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

    public void update(
            String title,
            String url,
            String username,
            String encryptedPassword,
            String notes,
            int folderId,
            LocalDateTime updatedAt
    ) {
        this.title = Objects.requireNonNull(title);
        this.url = url;
        this.username = Objects.requireNonNull(username);
        this.encryptedPassword = Objects.requireNonNull(encryptedPassword);
        this.notes = notes;
        this.folderId = folderId;
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }
}