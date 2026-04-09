package org.service.passwordman.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class PasswordEntry {
    private final int id;
    private final int userId;
    private String title;
    private String url;
    private String username;
    private String encryptedPassword;
    private String notes;
    private int folderId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PasswordEntry(
            int id,
            int userId,
            String title,
            String url,
            String username,
            String encryptedPassword,
            String notes,
            int folderId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.title = Objects.requireNonNull(title);
        this.url = url;
        this.username = Objects.requireNonNull(username);
        this.encryptedPassword = Objects.requireNonNull(encryptedPassword);
        this.notes = notes;
        this.folderId = folderId;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
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

    public String getEncryptedPassword() {
        return encryptedPassword;
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