package org.service.passwordman.application.port;

import org.service.passwordman.application.security.TokenPayload;

public interface TokenService {

    String generateAccessToken(TokenPayload payload);

    TokenPayload parseAccessToken(String token);
}