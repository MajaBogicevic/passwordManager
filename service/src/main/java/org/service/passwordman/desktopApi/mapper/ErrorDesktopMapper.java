package org.service.passwordman.desktopApi.mapper;

import org.service.passwordman.desktopApi.response.ApiErrorResponse;
import org.service.passwordman.domain.exception.EntryNotFoundException;
import org.service.passwordman.domain.exception.FolderNotFoundException;
import org.service.passwordman.domain.exception.InvalidCredentialsException;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.UserExistsException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;

public class ErrorDesktopMapper {

    public ApiErrorResponse map(Throwable ex) {
        if (ex instanceof ValidationException) {
            return new ApiErrorResponse("VALIDATION_ERROR", ex.getMessage());
        }
        if (ex instanceof UserExistsException) {
            return new ApiErrorResponse("USER_EXISTS", ex.getMessage());
        }
        if (ex instanceof InvalidCredentialsException) {
            return new ApiErrorResponse("INVALID_CREDENTIALS", ex.getMessage());
        }
        if (ex instanceof VaultSessionExpiredException) {
            return new ApiErrorResponse("VAULT_LOCKED", ex.getMessage());
        }
        if (ex instanceof UnauthorizedVaultAccessException) {
            return new ApiErrorResponse("UNAUTHORIZED", ex.getMessage());
        }
        if (ex instanceof FolderNotFoundException) {
            return new ApiErrorResponse("FOLDER_NOT_FOUND", ex.getMessage());
        }
        if (ex instanceof EntryNotFoundException) {
            return new ApiErrorResponse("ENTRY_NOT_FOUND", ex.getMessage());
        }
        if (ex instanceof IllegalStateException) {
            return new ApiErrorResponse("ILLEGAL_STATE", ex.getMessage());
        }

        return new ApiErrorResponse("INTERNAL_ERROR", ex.getMessage() != null ? ex.getMessage() : "Unexpected error.");
    }
}