package org.service.passwordman.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.service.passwordman.application.port.TokenService;
import org.service.passwordman.application.security.TokenPayload;
import org.service.passwordman.domain.exception.TokenValidationException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class JwtTokenService implements TokenService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMillis;

    public JwtTokenService(String secret, long accessTokenExpirationMillis) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
    }

    @Override
    public String generateAccessToken(TokenPayload tokenPayload) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpirationMillis);
        String jwtTokenId = tokenPayload.getJwtTokenId();

        if (jwtTokenId == null || jwtTokenId.isBlank()) {
            jwtTokenId = UUID.randomUUID().toString();
        }

        return Jwts.builder()
                .subject(String.valueOf(tokenPayload.getUserId()))
                .claim("username", tokenPayload.getUsername())
                .id(jwtTokenId)
                .issuedAt(now)
                .expiration(expiration)
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

            String subject = claims.getSubject();
            String username = claims.get("username", String.class);
            String jwtTokenId = claims.getId();

            if (subject == null || subject.isBlank()) {
                throw new TokenValidationException("Token subject is missing.");
            }

            if (username == null || username.isBlank()) {
                throw new TokenValidationException("Token username is missing.");
            }

            if(jwtTokenId == null || jwtTokenId.isBlank()) {
                throw new TokenValidationException("Token ID is missing.");
            }

            int userId;
            try {
                userId = Integer.parseInt(subject);
            } catch (NumberFormatException ex) {
                throw new TokenValidationException("Token subject is invalid.", ex);
            }

            return new TokenPayload(userId, username, jwtTokenId);

        } catch (JwtException | IllegalArgumentException ex) {
            throw new TokenValidationException("Access token is invalid.", ex);
        }
    }
}