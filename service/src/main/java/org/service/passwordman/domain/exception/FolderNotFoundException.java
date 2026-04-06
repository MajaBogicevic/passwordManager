package org.service.passwordman.domain.exception;

public class FolderNotFoundException extends RuntimeException {
    public FolderNotFoundException(String message) {
        super("Folder not found: " + message);
    }
}
