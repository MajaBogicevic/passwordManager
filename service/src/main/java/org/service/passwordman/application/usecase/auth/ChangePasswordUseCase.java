package org.service.passwordman.application.usecase.auth;

public interface ChangePasswordUseCase {
    void execute(int userId, String jwtTokenId, String oldPassword, String newPassword, String ipAddress);
}