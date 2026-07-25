package org.service.passwordman.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private final int id;
    private final String email;
    private final String username;
    private final String passwordHash;
    private final String keySalt;
    private final String wrappedDataKey;
    private String notes;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(
            int id,
            String email,
            String username,
            String passwordHash,
            String keySalt,
            String wrappedDataKey,
            String notes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.email = Objects.requireNonNull(email);
        this.username = Objects.requireNonNull(username);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.keySalt = Objects.requireNonNull(keySalt);
        this.wrappedDataKey = Objects.requireNonNull(wrappedDataKey);
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getKeySalt() {
        return keySalt;
    }

    public String getWrappedDataKey() {
        return wrappedDataKey;
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