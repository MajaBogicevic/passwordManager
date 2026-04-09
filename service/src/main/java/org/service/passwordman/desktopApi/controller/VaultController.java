package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.VaultHandler;
import org.service.passwordman.desktopApi.request.UnlockVaultRequest;
import org.service.passwordman.infrastructure.security.ClientIp;

import jakarta.servlet.http.HttpServletRequest;

public class VaultController {

    private final VaultHandler vaultHandler;
    private final ClientIp clientIp;

    public VaultController(VaultHandler vaultHandler, ClientIp clientIp) {
        this.vaultHandler = vaultHandler;
        this.clientIp = clientIp;
    }

    public Object unlock(UnlockVaultRequest request, HttpServletRequest httpRequest) {
        return vaultHandler.unlockSafe(request, clientIp.resolve(httpRequest));
    }

    public Object unlock(UnlockVaultRequest request, String clientIpAddress) {
        return vaultHandler.unlockSafe(request, clientIpAddress);
    }

    public Object lock(HttpServletRequest httpRequest) {
        return vaultHandler.lockSafe(clientIp.resolve(httpRequest));
    }

    public Object lock(String clientIpAddress) {
        return vaultHandler.lockSafe(clientIpAddress);
    }

    public Object autoLock(HttpServletRequest httpRequest) {
        return vaultHandler.autoLockSafe(clientIp.resolve(httpRequest));
    }

    public Object autoLock(String clientIpAddress) {
        return vaultHandler.autoLockSafe(clientIpAddress);
    }
}