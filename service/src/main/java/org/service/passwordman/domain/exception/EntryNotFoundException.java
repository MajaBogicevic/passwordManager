package org.service.passwordman.domain.exception;

public class EntryNotFoundException extends RuntimeException {
    public EntryNotFoundException(String entryId) {
        super("password entry not found: " + entryId);
    }
}