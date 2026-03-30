package org.service.passwordman.domain.model;

public class Vault {
    private int id;
    private int userId;
    private Boolean vaultLocked;

    public Vault(int id, int userId, Boolean vaultLocked) {
        this.id = id;
        this.userId = userId;
        this.vaultLocked = vaultLocked;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public Boolean getVaultLocked() {
        return vaultLocked;
    }

    public void lock() {
        this.vaultLocked = true;
    }

    public void unlock() {
        this.vaultLocked = false;
    }
}