package org.service.passwordman.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.service.passwordman.application.port.TokenService;
import org.service.passwordman.application.security.TokenPayload;
import org.service.passwordman.domain.exception.InvalidCredentialsException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTokenService implements TokenService {

    private final SecretKey secretKey;
    private final long expirationMillis;

    public JwtTokenService(String secret, long expirationMillis) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret is required.");
        }

        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long.");
        }

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    @Override
    public String generateAccessToken(TokenPayload payload) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(String.valueOf(payload.getUserId()))
                .claim("username", payload.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMillis))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public TokenPayload parseAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            int userId = Integer.parseInt(claims.getSubject());
            String username = claims.get("username", String.class);

            return new TokenPayload(userId, username);
        } catch (Exception ex) {
            throw new InvalidCredentialsException();
        }
    }
}