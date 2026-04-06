package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.AuthHandler;
import org.service.passwordman.desktopApi.request.ChangeMasterPasswordRequest;
import org.service.passwordman.desktopApi.request.ChangeLoginPasswordRequest;
import org.service.passwordman.desktopApi.request.LoginRequest;
import org.service.passwordman.desktopApi.request.RegisterRequest;

public class AuthController {

    private final AuthHandler authHandler;

    public AuthController(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public Object register(RegisterRequest request) {
        return authHandler.registerSafe(request);
    }

    public Object login(LoginRequest request) {
        return authHandler.loginSafe(request);
    }

    public Object changeMasterPassword(ChangeMasterPasswordRequest request) {
        return authHandler.changeMasterPasswordSafe(request);
    }

    public Object changeLoginPassword(ChangeLoginPasswordRequest request) {
        return authHandler.changeLoginPasswordSafe(request);
    }
}