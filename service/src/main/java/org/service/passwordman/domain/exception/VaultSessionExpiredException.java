package org.service.passwordman.domain.exception;

public class VaultSessionExpiredException extends RuntimeException {
    public VaultSessionExpiredException() {
        super("Vault session expired. Please unlock the vault again.");
    }
}