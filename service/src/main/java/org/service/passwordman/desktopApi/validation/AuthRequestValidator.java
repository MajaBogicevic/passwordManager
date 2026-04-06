package org.service.passwordman.desktopApi.validation;

import org.service.passwordman.desktopApi.request.ChangeMasterPasswordRequest;
import org.service.passwordman.desktopApi.request.ChangeLoginPasswordRequest;
import org.service.passwordman.desktopApi.request.LoginRequest;
import org.service.passwordman.desktopApi.request.RegisterRequest;
import org.service.passwordman.desktopApi.request.UnlockVaultRequest;
import org.service.passwordman.domain.exception.ValidationException;

public class AuthRequestValidator {

    public void validateRegister(RegisterRequest request) {
        if (request == null) {
            throw new ValidationException("Register request must not be null.");
        }
        if (isBlank(request.getEmail())) {
            throw new ValidationException("Email is required.");
        }
        if (isBlank(request.getUsername())) {
            throw new ValidationException("Username is required.");
        }
        if (isBlank(request.getLoginPassword())) {
            throw new ValidationException("Login password is required.");
        }
        if (isBlank(request.getMasterPassword())) {
            throw new ValidationException("Master password is required.");
        }
    }

    public void validateLogin(LoginRequest request) {
        if (request == null) {
            throw new ValidationException("Login request must not be null.");
        }
        if (isBlank(request.getUsername())) {
            throw new ValidationException("Username is required.");
        }
        if (isBlank(request.getLoginPassword())) {
            throw new ValidationException("Login password is required.");
        }
    }

    public void validateUnlock(UnlockVaultRequest request) {
        if (request == null) {
            throw new ValidationException("Unlock vault request must not be null.");
        }
        if (isBlank(request.getMasterPassword())) {
            throw new ValidationException("Master password is required.");
        }
    }

    public void validateChangeMasterPassword(ChangeMasterPasswordRequest request) {
        if (request == null) {
            throw new ValidationException("Change master password request must not be null.");
        }
        if (isBlank(request.getOldMasterPassword())) {
            throw new ValidationException("Old master password is required.");
        }
        if (isBlank(request.getNewMasterPassword())) {
            throw new ValidationException("New master password is required.");
        }
    }

    public void validateChangeLoginPassword(ChangeLoginPasswordRequest request) {
        if (request == null) {
            throw new ValidationException("Change login password request must not be null.");
        }
        if (isBlank(request.getOldLoginPassword())) {
            throw new ValidationException("Old login password is required.");
        }
        if (isBlank(request.getNewLoginPassword())) {
            throw new ValidationException("New login password is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}