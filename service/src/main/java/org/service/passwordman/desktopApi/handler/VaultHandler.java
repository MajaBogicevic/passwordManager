package org.service.passwordman.desktopApi.handler;

import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.application.usecase.vault.LockVaultUseCase;
import org.service.passwordman.application.usecase.vault.UnlockVaultUseCase;
import org.service.passwordman.desktopApi.mapper.AuthDesktopMapper;
import org.service.passwordman.desktopApi.request.UnlockVaultRequest;
import org.service.passwordman.desktopApi.response.AuthResponse;
import org.service.passwordman.desktopApi.validation.AuthRequestValidator;

public class VaultHandler {

    private final UnlockVaultUseCase unlockVaultUseCase;
    private final LockVaultUseCase lockVaultUseCase;
    private final AutoLockUseCase autoLockUseCase;
    private final AuthDesktopMapper authDesktopMapper;
    private final AuthRequestValidator authRequestValidator;
    private final ApiHandler apiHandler;

    public VaultHandler(
            UnlockVaultUseCase unlockVaultUseCase,
            LockVaultUseCase lockVaultUseCase,
            AutoLockUseCase autoLockUseCase,
            AuthDesktopMapper authDesktopMapper,
            AuthRequestValidator authRequestValidator,
            ApiHandler apiHandler
    ) {
        this.unlockVaultUseCase = unlockVaultUseCase;
        this.lockVaultUseCase = lockVaultUseCase;
        this.autoLockUseCase = autoLockUseCase;
        this.authDesktopMapper = authDesktopMapper;
        this.authRequestValidator = authRequestValidator;
        this.apiHandler = apiHandler;
    }

    public AuthResponse unlock(UnlockVaultRequest request) {
        authRequestValidator.validateUnlock(request);

        unlockVaultUseCase.execute(
                request.getUserId(),
                request.getMasterPassword()
        );

        return authDesktopMapper.success("Vault successfully unlocked.");
    }

    public Object unlockSafe(UnlockVaultRequest request) {
        return apiHandler.execute(() -> unlock(request));
    }

    public AuthResponse lock(int userId) {
        lockVaultUseCase.execute(userId);
        return authDesktopMapper.success("Vault successfully locked.");
    }

    public Object lockSafe(int userId) {
        return apiHandler.execute(() -> lock(userId));
    }

    public AuthResponse autoLock(int userId) {
        autoLockUseCase.execute(userId);
        return authDesktopMapper.success("Vault auto-lock executed.");
    }

    public Object autoLockSafe(int userId) {
        return apiHandler.execute(() -> autoLock(userId));
    }
}