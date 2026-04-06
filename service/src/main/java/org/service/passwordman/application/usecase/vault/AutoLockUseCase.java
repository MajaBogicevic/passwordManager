package org.service.passwordman.application.usecase.vault;

public interface AutoLockUseCase {
    void execute(int userId, String jwtTokenId, String ipAddress);
    void ensureVaultIsActive(int userId, String jwtTokenId, String ipAddress);
    void refreshActivity(int userId, String jwtTokenId);
}