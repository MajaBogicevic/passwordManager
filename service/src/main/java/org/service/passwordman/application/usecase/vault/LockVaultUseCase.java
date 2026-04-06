package org.service.passwordman.application.usecase.vault;


public interface LockVaultUseCase {
    void execute(int userId, String jwtTokenId, String ipAddress);
}