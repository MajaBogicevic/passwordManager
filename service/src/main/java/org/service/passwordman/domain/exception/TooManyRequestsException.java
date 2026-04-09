package org.service.passwordman.domain.exception;

public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException() {
        super("Too many requests. Please try again later.");
    }

    public TooManyRequestsException(String message) {
        super(message);
    }
}