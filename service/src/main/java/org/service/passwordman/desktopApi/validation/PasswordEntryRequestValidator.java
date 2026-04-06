package org.service.passwordman.desktopApi.validation;

import org.service.passwordman.desktopApi.request.CreatePasswordEntryRequest;
import org.service.passwordman.desktopApi.request.UpdatePasswordEntryRequest;
import org.service.passwordman.desktopApi.request.SearchPasswordEntriesRequest;
import org.service.passwordman.domain.exception.ValidationException;

public class PasswordEntryRequestValidator {

    public void validateCreate(CreatePasswordEntryRequest request) {
        if (request == null) {
            throw new ValidationException("Create password entry request must not be null.");
        }
        validateCommon(
                request.getTitle(),
                request.getUsername(),
                request.getPlainPassword(),
                request.getFolderId()
        );
    }

    public void validateUpdate(UpdatePasswordEntryRequest request) {
        if (request == null) {
            throw new ValidationException("Update password entry request must not be null.");
        }
        if (request.getEntryId() <= 0) {
            throw new ValidationException("Entry id must be greater than 0.");
        }
        validateCommon(
                request.getTitle(),
                request.getUsername(),
                request.getPlainPassword(),
                request.getFolderId()
        );
    }

    public void validateGet(int userId, int entryId) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }
        if (entryId <= 0) {
            throw new ValidationException("Entry id must be greater than 0.");
        }
    }

    public void validateDelete(int userId, int entryId) {
        validateGet(userId, entryId);
    }

    public void validateReveal(int userId, int entryId) {
        validateGet(userId, entryId);
    }

    public void validateGetByFolder(int userId, int folderId) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }
        if (folderId <= 0) {
            throw new ValidationException("Folder id must be greater than 0.");
        }
    }

    private void validateCommon(
            String title,
            String username,
            String plainPassword,
            int folderId
    ) {
        if (isBlank(title)) {
            throw new ValidationException("Title is required.");
        }
        if (isBlank(username)) {
            throw new ValidationException("Username is required.");
        }
        if (isBlank(plainPassword)) {
            throw new ValidationException("Password is required.");
        }
        if (folderId <= 0) {
            throw new ValidationException("Folder id must be greater than 0.");
        }
    }

    public void validateSearch(SearchPasswordEntriesRequest request) {
        if (request == null) {
            throw new ValidationException("Search request must not be null.");
        }

        if (request.getTitleQuery() == null) {
            throw new ValidationException("Search query must not be null.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}