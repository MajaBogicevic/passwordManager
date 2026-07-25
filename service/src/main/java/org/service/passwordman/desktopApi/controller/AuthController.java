package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.AuthHandler;
import org.service.passwordman.desktopApi.request.ChangePasswordRequest;
import org.service.passwordman.desktopApi.request.LoginRequest;
import org.service.passwordman.desktopApi.request.LogoutRequest;
import org.service.passwordman.desktopApi.request.RefreshTokenRequest;
import org.service.passwordman.desktopApi.request.RegisterRequest;
import org.service.passwordman.infrastructure.security.ClientIp;

import jakarta.servlet.http.HttpServletRequest;

public class AuthController {

    private final AuthHandler authHandler;
    private final ClientIp clientIp;

    public AuthController(AuthHandler authHandler, ClientIp clientIp) {
        this.authHandler = authHandler;
        this.clientIp = clientIp;
    }

    public Object register(RegisterRequest request, HttpServletRequest httpRequest) {
        return authHandler.registerSafe(request, clientIp.resolve(httpRequest));
    }

    public Object register(RegisterRequest request, String clientIpAddress) {
        return authHandler.registerSafe(request, clientIpAddress);
    }

    public Object login(LoginRequest request, HttpServletRequest httpRequest) {
        return authHandler.loginSafe(request, clientIp.resolve(httpRequest));
    }

    public Object login(LoginRequest request, String clientIpAddress) {
        return authHandler.loginSafe(request, clientIpAddress);
    }

    public Object changePassword(ChangePasswordRequest request, HttpServletRequest httpRequest) {
        return authHandler.changePasswordSafe(request, clientIp.resolve(httpRequest));
    }

    public Object changePassword(ChangePasswordRequest request, String clientIpAddress) {
        return authHandler.changePasswordSafe(request, clientIpAddress);
    }

    public Object logout(LogoutRequest request, HttpServletRequest httpRequest) {
        return authHandler.logoutSafe(request, clientIp.resolve(httpRequest));
    }

    public Object logout(LogoutRequest request, String clientIpAddress) {
        return authHandler.logoutSafe(request, clientIpAddress);
    }

    public Object refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        return authHandler.refreshTokenSafe(request, clientIp.resolve(httpRequest));
    }

    public Object refreshToken(RefreshTokenRequest request, String clientIpAddress) {
        return authHandler.refreshTokenSafe(request, clientIpAddress);
    }
}