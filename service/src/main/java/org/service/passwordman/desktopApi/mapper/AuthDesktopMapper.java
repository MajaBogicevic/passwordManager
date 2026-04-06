package org.service.passwordman.desktopApi.mapper;

import org.service.passwordman.desktopApi.response.AuthResponse;

public class AuthDesktopMapper {

    public AuthResponse success(String message) {
        return new AuthResponse(true, message);
    }

    public AuthResponse failure(String message) {
        return new AuthResponse(false, message);
    }
}