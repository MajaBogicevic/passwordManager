package org.service.passwordman.desktopApi.validation;

import java.util.regex.Pattern;

import org.service.passwordman.desktopApi.request.ChangeLoginPasswordRequest;
import org.service.passwordman.desktopApi.request.ChangeMasterPasswordRequest;
import org.service.passwordman.desktopApi.request.LoginRequest;
import org.service.passwordman.desktopApi.request.RefreshTokenRequest;
import org.service.passwordman.desktopApi.request.RegisterRequest;
import org.service.passwordman.desktopApi.request.UnlockVaultRequest;
import org.service.passwordman.domain.exception.ValidationException;

public class AuthRequestValidator {

    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_USERNAME_LENGTH = 100;
    private static final int MAX_PASSWORD_LENGTH = 512;
    private static final int MAX_NOTES_LENGTH = 2000;

    private static final Pattern SIMPLE_EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public void validateRegister(RegisterRequest request) {
        if (request == null) {
            throw new ValidationException("Register request must not be null.");
        }

        requireNotBlank(request.getEmail(), "Email is required.");
        requireNotBlank(request.getUsername(), "Username is required.");
        requireNotBlank(request.getLoginPassword(), "Login password is required.");
        requireNotBlank(request.getMasterPassword(), "Master password is required.");

        requireMaxLength(request.getEmail(), MAX_EMAIL_LENGTH, "Email is too long.");
        requireMaxLength(request.getUsername(), MAX_USERNAME_LENGTH, "Username is too long.");
        requireMaxLength(request.getLoginPassword(), MAX_PASSWORD_LENGTH, "Login password is too long.");
        requireMaxLength(request.getMasterPassword(), MAX_PASSWORD_LENGTH, "Master password is too long.");
        requireMaxLength(request.getNotes(), MAX_NOTES_LENGTH, "Notes are too long.");

        if (!SIMPLE_EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new ValidationException("Email format is invalid.");
        }
    }

    public void validateLogin(LoginRequest request) {
        if (request == null) {
            throw new ValidationException("Login request must not be null.");
        }

        requireNotBlank(request.getUsername(), "Username is required.");
        requireNotBlank(request.getLoginPassword(), "Login password is required.");

        requireMaxLength(request.getUsername(), MAX_USERNAME_LENGTH, "Username is too long.");
        requireMaxLength(request.getLoginPassword(), MAX_PASSWORD_LENGTH, "Login password is too long.");
    }

    public void validateUnlock(UnlockVaultRequest request) {
        if (request == null) {
            throw new ValidationException("Unlock vault request must not be null.");
        }

        requireNotBlank(request.getMasterPassword(), "Master password is required.");
        requireMaxLength(request.getMasterPassword(), MAX_PASSWORD_LENGTH, "Master password is too long.");
    }

    public void validateChangeMasterPassword(ChangeMasterPasswordRequest request) {
        if (request == null) {
            throw new ValidationException("Change master password request must not be null.");
        }

        requireNotBlank(request.getOldMasterPassword(), "Old master password is required.");
        requireNotBlank(request.getNewMasterPassword(), "New master password is required.");

        requireMaxLength(request.getOldMasterPassword(), MAX_PASSWORD_LENGTH, "Old master password is too long.");
        requireMaxLength(request.getNewMasterPassword(), MAX_PASSWORD_LENGTH, "New master password is too long.");
    }

    public void validateChangeLoginPassword(ChangeLoginPasswordRequest request) {
        if (request == null) {
            throw new ValidationException("Change login password request must not be null.");
        }

        requireNotBlank(request.getOldLoginPassword(), "Old login password is required.");
        requireNotBlank(request.getNewLoginPassword(), "New login password is required.");

        requireMaxLength(request.getOldLoginPassword(), MAX_PASSWORD_LENGTH, "Old login password is too long.");
        requireMaxLength(request.getNewLoginPassword(), MAX_PASSWORD_LENGTH, "New login password is too long.");
    }

    public void validateLogout() {
    }

    public void validateRefreshToken(RefreshTokenRequest request) {
        if (request == null) {
            throw new ValidationException("Refresh token request must not be null.");
        }

        requireNotBlank(request.getRefreshToken(), "Refresh token is required.");
        requireMaxLength(request.getRefreshToken(), 4096, "Refresh token is too long.");
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