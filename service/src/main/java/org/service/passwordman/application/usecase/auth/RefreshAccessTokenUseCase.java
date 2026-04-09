package org.service.passwordman.application.usecase.auth;

import org.service.passwordman.application.security.AuthToken;

public interface RefreshAccessTokenUseCase {

    AuthToken execute(String refreshToken, String ipAddress);
}