package org.service.passwordman.domain.model;

import java.util.Objects;

public class Folder {
    private int id;
    private int userId;
    private String name;
 

    public Folder(int id, int userId, String name) {
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

    public void rename(String newName) {
        this.name = Objects.requireNonNull(newName);
    }
}