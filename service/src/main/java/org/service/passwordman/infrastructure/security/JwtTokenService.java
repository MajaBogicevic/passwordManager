package org.service.passwordman.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.service.passwordman.application.port.TokenService;
import org.service.passwordman.application.security.TokenPayload;
import org.service.passwordman.domain.exception.TokenValidationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtTokenService implements TokenService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMillis;
    private final long refreshTokenExpirationMillis;

    public JwtTokenService(
            String secret,
            long accessTokenExpirationMillis,
            long refreshTokenExpirationMillis
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
    }

    @Override
    public String generateAccessToken(TokenPayload tokenPayload) {
        return generateToken(tokenPayload, accessTokenExpirationMillis, "access");
    }

    @Override
    public String generateRefreshToken(TokenPayload tokenPayload) {
        return generateToken(tokenPayload, refreshTokenExpirationMillis, "refresh");
    }

    private String generateToken(TokenPayload tokenPayload, long expirationMillis, String tokenType) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        String jwtTokenId = tokenPayload.getJwtTokenId();
        if (jwtTokenId == null || jwtTokenId.isBlank()) {
            jwtTokenId = UUID.randomUUID().toString();
        }

        String sessionId = tokenPayload.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Session ID is required for token generation.");
        }

        return Jwts.builder()
                .subject(String.valueOf(tokenPayload.getUserId()))
                .claim("username", tokenPayload.getUsername())
                .claim("sessionId", sessionId)
                .claim("tokenType", tokenType)
                .id(jwtTokenId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public TokenPayload parseAccessToken(String token) {
        TokenPayload payload = parseToken(token);

        if (!"access".equals(payload.getTokenType())) {
            throw new TokenValidationException("Token is not an access token.");
        }

        return payload;
    }

    @Override
    public TokenPayload parseRefreshToken(String token) {
        TokenPayload payload = parseToken(token);

        if (!"refresh".equals(payload.getTokenType())) {
            throw new TokenValidationException("Token is not a refresh token.");
        }

        return payload;
    }

    private TokenPayload parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();
            String username = claims.get("username", String.class);
            String jwtTokenId = claims.getId();
            String sessionId = claims.get("sessionId", String.class);
            String tokenType = claims.get("tokenType", String.class);

            if (subject == null || subject.isBlank()) {
                throw new TokenValidationException("Token subject is missing.");
            }

            if (username == null || username.isBlank()) {
                throw new TokenValidationException("Token username is missing.");
            }

            if (jwtTokenId == null || jwtTokenId.isBlank()) {
                throw new TokenValidationException("Token ID is missing.");
            }

            if (sessionId == null || sessionId.isBlank()) {
                throw new TokenValidationException("Token session ID is missing.");
            }

            if (tokenType == null || tokenType.isBlank()) {
                throw new TokenValidationException("Token type is missing.");
            }

            int userId;
            try {
                userId = Integer.parseInt(subject);
            } catch (NumberFormatException ex) {
                throw new TokenValidationException("Token subject is invalid.", ex);
            }

            return new TokenPayload(
                    userId,
                    username,
                    jwtTokenId,
                    sessionId,
                    tokenType
            );

        } catch (JwtException | IllegalArgumentException ex) {
            throw new TokenValidationException("Token is invalid.", ex);
        }
    }

    @Override
    public long extractExpirationMillis(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();

            if (expiration == null) {
                throw new TokenValidationException("Token expiration is missing.");
            }

            return expiration.getTime();

        } catch (JwtException | IllegalArgumentException ex) {
            throw new TokenValidationException("Token is invalid.", ex);
        }
    }

    @Override
    public long extractIssuedAtMillis(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date issuedAt = claims.getIssuedAt();

            if (issuedAt == null) {
                throw new TokenValidationException("Token issued-at is missing.");
            }

            return issuedAt.getTime();

        } catch (JwtException | IllegalArgumentException ex) {
            throw new TokenValidationException("Token is invalid.", ex);
        }
    }
}