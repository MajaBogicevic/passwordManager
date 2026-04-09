package org.service.passwordman.application.service.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.RateLimitStore;
import org.service.passwordman.application.port.RefreshTokenRecord;
import org.service.passwordman.application.port.RefreshTokenStore;
import org.service.passwordman.application.port.TokenService;
import org.service.passwordman.application.security.AuthToken;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.security.TokenPayload;
import org.service.passwordman.application.usecase.auth.RefreshAccessTokenUseCase;
import org.service.passwordman.domain.exception.TokenValidationException;
import org.service.passwordman.domain.exception.TooManyRequestsException;
import org.service.passwordman.domain.model.SecurityEventType;

public class RefreshAccessTokenService implements RefreshAccessTokenUseCase {

    private final TokenService tokenService;
    private final RefreshTokenStore refreshTokenStore;
    private final AuditLogger auditLogger;
    private final RateLimitStore rateLimitStore;
    private final int maxAttempts;
    private final long blockDurationMillis;

    public RefreshAccessTokenService(
            TokenService tokenService,
            RefreshTokenStore refreshTokenStore,
            AuditLogger auditLogger,
            RateLimitStore rateLimitStore,
            int maxAttempts,
            long blockDurationMillis
    ) {
        this.tokenService = tokenService;
        this.refreshTokenStore = refreshTokenStore;
        this.auditLogger = auditLogger;
        this.rateLimitStore = rateLimitStore;
        this.maxAttempts = maxAttempts;
        this.blockDurationMillis = blockDurationMillis;
    }

    @Override
    public AuthToken execute(String refreshToken, String ipAddress) {
        String rateLimitKey = buildRateLimitKey(refreshToken, ipAddress);

        if (rateLimitStore.isBlocked(rateLimitKey)) {
            auditLogger.log(SecurityAuditEvent.rateLimited(
                    0,
                    SecurityEventType.REFRESH_TOKEN,
                    ipAddress,
                    null,
                    "Refresh token rate limit triggered."
            ));
            throw new TooManyRequestsException("Too many failed refresh attempts. Please try again later.");
        }

        TokenPayload refreshPayload;
        try {
            refreshPayload = tokenService.parseRefreshToken(refreshToken);
        } catch (TokenValidationException ex) {
            rateLimitStore.recordFailure(rateLimitKey, maxAttempts, blockDurationMillis);
            auditLogger.log(SecurityAuditEvent.failure(
                    0,
                    SecurityEventType.REFRESH_TOKEN,
                    "INVALID_REFRESH_TOKEN",
                    ipAddress,
                    null,
                    "Refresh token parsing failed."
            ));
            throw ex;
        }

        RefreshTokenRecord stored = refreshTokenStore.findByTokenId(refreshPayload.getJwtTokenId())
                .orElseThrow(() -> {
                    rateLimitStore.recordFailure(rateLimitKey, maxAttempts, blockDurationMillis);
                    auditLogger.log(SecurityAuditEvent.failure(
                            refreshPayload.getUserId(),
                            SecurityEventType.REFRESH_TOKEN,
                            "INACTIVE_REFRESH_TOKEN",
                            ipAddress,
                            refreshPayload.getSessionId(),
                            "Refresh token was not found in active store."
                    ));
                    return new TokenValidationException("Refresh token is not active.");
                });

        long now = System.currentTimeMillis();

        if (stored.getUserId() != refreshPayload.getUserId()) {
            rateLimitStore.recordFailure(rateLimitKey, maxAttempts, blockDurationMillis);
            auditLogger.log(SecurityAuditEvent.securityAlert(
                    refreshPayload.getUserId(),
                    SecurityEventType.REFRESH_TOKEN,
                    "REFRESH_USER_MISMATCH",
                    ipAddress,
                    refreshPayload.getSessionId(),
                    "Stored refresh token user mismatch detected."
            ));
            throw new TokenValidationException("Refresh token user mismatch.");
        }

        if (!stored.getTokenFamilyId().equals(refreshPayload.getSessionId())) {
            rateLimitStore.recordFailure(rateLimitKey, maxAttempts, blockDurationMillis);
            auditLogger.log(SecurityAuditEvent.securityAlert(
                    refreshPayload.getUserId(),
                    SecurityEventType.REFRESH_TOKEN,
                    "REFRESH_SESSION_MISMATCH",
                    ipAddress,
                    refreshPayload.getSessionId(),
                    "Stored refresh token session mismatch detected."
            ));
            throw new TokenValidationException("Refresh token session mismatch.");
        }

        if (stored.isExpired(now)) {
            rateLimitStore.recordFailure(rateLimitKey, maxAttempts, blockDurationMillis);
            auditLogger.log(SecurityAuditEvent.failure(
                    refreshPayload.getUserId(),
                    SecurityEventType.REFRESH_TOKEN,
                    "REFRESH_TOKEN_EXPIRED",
                    ipAddress,
                    refreshPayload.getSessionId(),
                    "Refresh token expired."
            ));
            throw new TokenValidationException("Refresh token expired.");
        }

        if (stored.isConsumed()) {
            refreshTokenStore.revokeFamily(stored.getTokenFamilyId());
            rateLimitStore.recordFailure(rateLimitKey, maxAttempts, blockDurationMillis);
            auditLogger.log(SecurityAuditEvent.securityAlert(
                    refreshPayload.getUserId(),
                    SecurityEventType.REFRESH_TOKEN,
                    "REFRESH_TOKEN_REUSE_DETECTED",
                    ipAddress,
                    refreshPayload.getSessionId(),
                    "Refresh token reuse detected. Token family revoked."
            ));
            throw new TokenValidationException("Refresh token reuse detected.");
        }

        if (stored.isRevoked() || stored.isFamilyRevoked()) {
            rateLimitStore.recordFailure(rateLimitKey, maxAttempts, blockDurationMillis);
            auditLogger.log(SecurityAuditEvent.failure(
                    refreshPayload.getUserId(),
                    SecurityEventType.REFRESH_TOKEN,
                    "REFRESH_TOKEN_REVOKED",
                    ipAddress,
                    refreshPayload.getSessionId(),
                    "Refresh token was revoked."
            ));
            throw new TokenValidationException("Refresh token is revoked.");
        }

        String sessionId = stored.getTokenFamilyId();

        String newAccessToken = tokenService.generateAccessToken(
                new TokenPayload(
                        refreshPayload.getUserId(),
                        refreshPayload.getUsername(),
                        null,
                        sessionId,
                        "access"
                )
        );

        String newRefreshToken = tokenService.generateRefreshToken(
                new TokenPayload(
                        refreshPayload.getUserId(),
                        refreshPayload.getUsername(),
                        null,
                        sessionId,
                        "refresh"
                )
        );

        TokenPayload newRefreshPayload = tokenService.parseRefreshToken(newRefreshToken);
        long newRefreshExpiresAtMillis = tokenService.extractExpirationMillis(newRefreshToken);

        refreshTokenStore.rotate(
                refreshPayload.getJwtTokenId(),
                newRefreshPayload.getJwtTokenId(),
                refreshPayload.getUserId(),
                sessionId,
                newRefreshExpiresAtMillis
        );

        rateLimitStore.reset(rateLimitKey);
        auditLogger.log(SecurityAuditEvent.success(
                refreshPayload.getUserId(),
                SecurityEventType.REFRESH_TOKEN,
                ipAddress,
                sessionId,
                "Refresh token rotation succeeded."
        ));

        return new AuthToken(newAccessToken, newRefreshToken);
    }

    private String buildRateLimitKey(String refreshToken, String ipAddress) {
        String tokenFingerprint = sha256(refreshToken == null ? "" : refreshToken);
        String normalizedIp = ipAddress == null || ipAddress.isBlank() ? "unknown-ip" : ipAddress.trim();
        return "refresh:" + tokenFingerprint + ":" + normalizedIp;
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();
            for (byte b : hashed) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", ex);
        }
    }
}