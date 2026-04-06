package org.service.passwordman.domain.exception;

public class UnauthorizedVaultAccessException extends RuntimeException {
    public UnauthorizedVaultAccessException() {
        super("Can't access vault: unauthorized");
    }
}
