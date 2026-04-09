package org.service.passwordman.desktopApi.validation;

import org.service.passwordman.desktopApi.request.CreateFolderRequest;
import org.service.passwordman.desktopApi.request.RenameFolderRequest;
import org.service.passwordman.domain.exception.ValidationException;

public class FolderRequestValidator {

    private static final int MAX_FOLDER_NAME_LENGTH = 100;

    public void validateCreate(CreateFolderRequest request) {
        if (request == null) {
            throw new ValidationException("Create folder request must not be null.");
        }

        requireValidFolderName(request.getFolderName(), "Folder name is required.");
    }

    public void validateRename(RenameFolderRequest request) {
        if (request == null) {
            throw new ValidationException("Rename folder request must not be null.");
        }

        if (request.getFolderId() <= 0) {
            throw new ValidationException("Folder id must be greater than 0 when provided.");
        }

        requireValidFolderName(request.getNewName(), "New folder name is required.");
    }

    public void validateDelete(int folderId) {
        if (folderId <= 0) {
            throw new ValidationException("Folder id must be greater than 0.");
        }
    }

    private void requireValidFolderName(String value, String requiredMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(requiredMessage);
        }

        if (value.length() > MAX_FOLDER_NAME_LENGTH) {
            throw new ValidationException("Folder name is too long.");
        }
    }
}