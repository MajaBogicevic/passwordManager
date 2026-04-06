package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.VaultHandler;
import org.service.passwordman.desktopApi.request.UnlockVaultRequest;

public class VaultController {

    private final VaultHandler vaultHandler;

    public VaultController(VaultHandler vaultHandler) {
        this.vaultHandler = vaultHandler;
    }

    public Object unlock(UnlockVaultRequest request) {
        return vaultHandler.unlockSafe(request);
    }

    public Object lock(int userId) {
        return vaultHandler.lockSafe(userId);
    }

    public Object autoLock(int userId) {
        return vaultHandler.autoLockSafe(userId);
    }
}