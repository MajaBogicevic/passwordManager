package org.service.passwordman.application.usecase.vault;

public interface UnlockVaultUseCase {
    void execute(int userId, String masterPassword);
}