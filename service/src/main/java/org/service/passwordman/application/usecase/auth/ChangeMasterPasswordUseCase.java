package org.service.passwordman.application.usecase.auth;

public interface ChangeMasterPasswordUseCase {
    void execute(int userId, String jwtTokenId, String oldMasterPassword, String newMasterPassword, String ipAddress);
}