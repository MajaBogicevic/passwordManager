package org.service.passwordman.application.usecase.vault;

public interface AutoLockUseCase {
    void execute(int userId);
    void ensureVaultIsActive(int userId);
    void refreshActivity(int userId);
}