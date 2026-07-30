package org.service.passwordman.desktopApi.validation;

import org.service.passwordman.desktopApi.request.CreatePasswordEntryRequest;
import org.service.passwordman.desktopApi.request.SearchPasswordEntriesRequest;
import org.service.passwordman.desktopApi.request.UpdatePasswordEntryRequest;
import org.service.passwordman.domain.exception.ValidationException;

public class PasswordEntryRequestValidator {

    private static final int MAX_TITLE_LENGTH = 150;
    private static final int MAX_URL_LENGTH = 500;
    private static final int MAX_USERNAME_LENGTH = 150;
    private static final int MAX_PASSWORD_LENGTH = 1024;
    private static final int MAX_NOTES_LENGTH = 4000;
    private static final int MAX_SEARCH_LENGTH = 150;

    public void validateCreate(CreatePasswordEntryRequest request) {
        if (request == null) {
            throw new ValidationException("Create password entry request must not be null.");
        }

        requireNotBlank(request.getPlainPassword(), "Password is required.");

        validateCommon(
                request.getTitle(),
                request.getUrl(),
                request.getUsername(),
                request.getPlainPassword(),
                request.getNotes(),
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
                request.getUrl(),
                request.getUsername(),
                request.getPlainPassword(),
                request.getNotes(),
                request.getFolderId()
        );
    }

    public void validateGet(int entryId) {
        if (entryId <= 0) {
            throw new ValidationException("Entry id must be greater than 0.");
        }
    }

    public void validateDelete(int entryId) {
        validateGet(entryId);
    }

    public void validateReveal(int entryId) {
        validateGet(entryId);
    }

    public void validateGetByFolder(int folderId) {
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

        if (request.getTitleQuery().length() > MAX_SEARCH_LENGTH) {
            throw new ValidationException("Search query is too long.");
        }
    }

    private void validateCommon(
            String title,
            String url,
            String username,
            String plainPassword,
            String notes,
            int folderId
    ) {
        requireNotBlank(title, "Title is required.");
        requireNotBlank(username, "Username is required.");

        requireMaxLength(title, MAX_TITLE_LENGTH, "Title is too long.");
        requireMaxLength(url, MAX_URL_LENGTH, "URL is too long.");
        requireMaxLength(username, MAX_USERNAME_LENGTH, "Username is too long.");
        requireMaxLength(plainPassword, MAX_PASSWORD_LENGTH, "Password is too long.");
        requireMaxLength(notes, MAX_NOTES_LENGTH, "Notes are too long.");

        if (folderId < 0) {
            throw new ValidationException("Folder id must not be negative.");
        }
    }

    private void requireNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }

    private void requireMaxLength(String value, int maxLength, String message) {
        if (value != null && value.length() > maxLength) {
            throw new ValidationException(message);
        }
    }
}