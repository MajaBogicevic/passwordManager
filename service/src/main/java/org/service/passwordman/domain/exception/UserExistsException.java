package org.service.passwordman.domain.exception;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String message) {
        super("This username already exists: " + message);
    }
}
