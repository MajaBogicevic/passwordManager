package org.service.passwordman.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private int id;
    private String email;
    private String username;
    private String loginPasswordHash;
    private String masterPasswordHash;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(
            int id,
            String email,
            String username,
            String loginPasswordHash,
            String masterPasswordHash,
            String notes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.email = Objects.requireNonNull(email);
        this.username = Objects.requireNonNull(username);
        this.loginPasswordHash = Objects.requireNonNull(loginPasswordHash);
        this.masterPasswordHash = Objects.requireNonNull(masterPasswordHash);
        this.notes = notes;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getLoginPasswordHash() {
        return loginPasswordHash;
    }

    public String getMasterPasswordHash() {
        return masterPasswordHash;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateNotes(String notes, LocalDateTime updatedAt) {
        this.notes = notes;
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }
}