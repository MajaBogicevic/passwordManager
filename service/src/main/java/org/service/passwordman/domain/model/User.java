package org.service.passwordman.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private int id;
    private String name;
    private String surename;
    private String email;
    private String LoginPassword;
    private String masterPassword;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(int id, String name, String surename, String email, String loginPassword, String masterPassword, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.surename = surename;
        this.email = email;
        this.LoginPassword = loginPassword;
        this.masterPassword = masterPassword;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurename() {
        return surename;
    }

    public String getEmail() {
        return email;
    }

    public String getLoginPassword() {
        return LoginPassword;
    }

    public String getMasterPassword() {
        return masterPassword;
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