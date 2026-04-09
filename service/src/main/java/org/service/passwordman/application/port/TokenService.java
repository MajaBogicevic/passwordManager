package org.service.passwordman.application.port;

import org.service.passwordman.application.security.TokenPayload;

public interface TokenService {

    String generateAccessToken(TokenPayload payload);

    String generateRefreshToken(TokenPayload payload);

    TokenPayload parseAccessToken(String token);

    TokenPayload parseRefreshToken(String token);

    long extractExpirationMillis(String token);

    long extractIssuedAtMillis(String token);
}