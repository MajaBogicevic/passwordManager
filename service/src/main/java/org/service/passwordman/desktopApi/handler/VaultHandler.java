package org.service.passwordman.desktopApi.handler;

import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.application.usecase.vault.LockVaultUseCase;
import org.service.passwordman.application.usecase.vault.UnlockVaultUseCase;
import org.service.passwordman.desktopApi.mapper.AuthDesktopMapper;
import org.service.passwordman.desktopApi.request.UnlockVaultRequest;
import org.service.passwordman.desktopApi.response.AuthResponse;
import org.service.passwordman.desktopApi.validation.AuthRequestValidator;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;

public class VaultHandler {

    private final UnlockVaultUseCase unlockVaultUseCase;
    private final LockVaultUseCase lockVaultUseCase;
    private final AutoLockUseCase autoLockUseCase;
    private final AuthDesktopMapper authDesktopMapper;
    private final AuthRequestValidator authRequestValidator;
    private final ApiHandler apiHandler;
    private final CurrentUserProvider currentUserProvider;

    public VaultHandler(
            UnlockVaultUseCase unlockVaultUseCase,
            LockVaultUseCase lockVaultUseCase,
            AutoLockUseCase autoLockUseCase,
            AuthDesktopMapper authDesktopMapper,
            AuthRequestValidator authRequestValidator,
            ApiHandler apiHandler,
            CurrentUserProvider currentUserProvider
    ) {
        this.unlockVaultUseCase = unlockVaultUseCase;
        this.lockVaultUseCase = lockVaultUseCase;
        this.autoLockUseCase = autoLockUseCase;
        this.authDesktopMapper = authDesktopMapper;
        this.authRequestValidator = authRequestValidator;
        this.apiHandler = apiHandler;
        this.currentUserProvider = currentUserProvider;
    }

    public AuthResponse unlock(UnlockVaultRequest request, String clientIp) {
        authRequestValidator.validateUnlock(request);

        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        unlockVaultUseCase.execute(
                currentUserId,
                jwtTokenId,
                request.getMasterPassword(),
                clientIp
        );

        return authDesktopMapper.success("Vault successfully unlocked.");
    }

    public Object unlockSafe(UnlockVaultRequest request, String clientIp) {
        return apiHandler.execute(() -> unlock(request, clientIp));
    }

    public AuthResponse lock(String clientIp) {
        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        lockVaultUseCase.execute(currentUserId, jwtTokenId, clientIp);
        return authDesktopMapper.success("Vault successfully locked.");
    }

    public Object lockSafe(String clientIp) {
        return apiHandler.execute(() -> lock(clientIp));
    }

    public AuthResponse autoLock(String clientIp) {
        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        autoLockUseCase.execute(currentUserId, jwtTokenId, clientIp);
        return authDesktopMapper.success("Vault auto-lock executed.");
    }

    public Object autoLockSafe(String clientIp) {
        return apiHandler.execute(() -> autoLock(clientIp));
    }
}