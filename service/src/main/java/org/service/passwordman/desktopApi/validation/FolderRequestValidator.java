package org.service.passwordman.desktopApi.validation;

import org.service.passwordman.desktopApi.request.CreateFolderRequest;
import org.service.passwordman.desktopApi.request.RenameFolderRequest;
import org.service.passwordman.domain.exception.ValidationException;

public class FolderRequestValidator {

    public void validateCreate(CreateFolderRequest request) {
        if (request == null) {
            throw new ValidationException("Create folder request must not be null.");
        }
        if (request.getUserId() <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }
        if (isBlank(request.getFolderName())) {
            throw new ValidationException("Folder name is required.");
        }
    }

    public void validateRename(RenameFolderRequest request) {
        if (request == null) {
            throw new ValidationException("Rename folder request must not be null.");
        }
        if (request.getUserId() <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }
        if (request.getFolderId() <= 0) {
            throw new ValidationException("Folder id must be greater than 0.");
        }
        if (isBlank(request.getNewName())) {
            throw new ValidationException("New folder name is required.");
        }
    }

    public void validateDelete(int userId, int folderId) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }
        if (folderId <= 0) {
            throw new ValidationException("Folder id must be greater than 0.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}