package org.service.passwordman.application.usecase.vault;

public interface UnlockVaultUseCase {
    void execute(int userId, String jwtTokenId, String masterPassword, String ipAddress);
}