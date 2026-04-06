package org.service.passwordman.domain.exception;

public class VaultLockedException extends RuntimeException {

    public VaultLockedException() {
        super("Vault is locked.");
    }

    public VaultLockedException(String message) {
        super(message);
    }
}