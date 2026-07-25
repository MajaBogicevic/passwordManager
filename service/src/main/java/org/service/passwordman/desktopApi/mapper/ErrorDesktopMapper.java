package org.service.passwordman.desktopApi.mapper;

import org.service.passwordman.desktopApi.response.ApiErrorResponse;
import org.service.passwordman.domain.exception.EntryNotFoundException;
import org.service.passwordman.domain.exception.FolderNotFoundException;
import org.service.passwordman.domain.exception.InvalidCredentialsException;
import org.service.passwordman.domain.exception.TokenValidationException;
import org.service.passwordman.domain.exception.TooManyRequestsException;
import org.service.passwordman.domain.exception.UnauthorizedException;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.UserExistsException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.exception.VaultLockedException;
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
            return new ApiErrorResponse("INVALID_CREDENTIALS", "Invalid credentials.");
        }
        if (ex instanceof TooManyRequestsException) {
            return new ApiErrorResponse("RATE_LIMITED", ex.getMessage());
        }
        if (ex instanceof VaultLockedException) {
            return new ApiErrorResponse("VAULT_LOCKED", "Vault is locked. Unlock it before accessing this resource.");
        }
        if (ex instanceof VaultSessionExpiredException) {
            return new ApiErrorResponse("VAULT_LOCKED", "Vault is locked or session has expired.");
        }
        if (ex instanceof UnauthorizedVaultAccessException || ex instanceof UnauthorizedException) {
            return new ApiErrorResponse("UNAUTHORIZED", "You are not authorized to perform this action.");
        }
        if (ex instanceof TokenValidationException) {
            return new ApiErrorResponse("INVALID_TOKEN", "Token is invalid or expired.");
        }
        if (ex instanceof FolderNotFoundException) {
            return new ApiErrorResponse("FOLDER_NOT_FOUND", ex.getMessage());
        }
        if (ex instanceof EntryNotFoundException) {
            return new ApiErrorResponse("ENTRY_NOT_FOUND", ex.getMessage());
        }
        if (ex instanceof IllegalStateException) {
            return new ApiErrorResponse("ILLEGAL_STATE", "Operation cannot be completed in the current state.");
        }

        return new ApiErrorResponse("INTERNAL_ERROR", "An unexpected error occurred.");
    }
}