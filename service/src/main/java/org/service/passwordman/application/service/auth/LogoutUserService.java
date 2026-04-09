package org.service.passwordman.application.service.auth;

import java.time.ZoneId;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.RefreshTokenStore;
import org.service.passwordman.application.port.TokenBlacklistStore;
import org.service.passwordman.application.port.UserAuthInvalidationStore;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.usecase.auth.LogoutUserUseCase;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.SecurityEventType;

public class LogoutUserService implements LogoutUserUseCase {

    private final TokenBlacklistStore tokenBlacklistStore;
    private final RefreshTokenStore refreshTokenStore;
    private final AuditLogger auditLogger;
    private final Clock clock;
    private final UserAuthInvalidationStore userAuthInvalidationStore;

    public LogoutUserService(
            TokenBlacklistStore tokenBlacklistStore,
            RefreshTokenStore refreshTokenStore,
            AuditLogger auditLogger,
            Clock clock,
            UserAuthInvalidationStore userAuthInvalidationStore
    ) {
        this.tokenBlacklistStore = tokenBlacklistStore;
        this.refreshTokenStore = refreshTokenStore;
        this.auditLogger = auditLogger;
        this.clock = clock;
        this.userAuthInvalidationStore = userAuthInvalidationStore;
    }

    @Override
    public void execute(
            int userId,
            String sessionId,
            String jwtTokenId,
            long expiresAtMillis,
            boolean allSessions,
            String ipAddress
    ) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (jwtTokenId == null || jwtTokenId.isBlank()) {
            throw new ValidationException("JWT token id is required.");
        }

        if (sessionId == null || sessionId.isBlank()) {
            throw new ValidationException("Session id is required.");
        }

        tokenBlacklistStore.blacklist(jwtTokenId, expiresAtMillis);

        if (allSessions) {
            refreshTokenStore.revokeAllByUserId(userId);

            long cutoffMillis = clock.now()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            userAuthInvalidationStore.invalidateAllTokensForUser(userId, cutoffMillis);

            auditLogger.log(SecurityAuditEvent.success(
                    userId,
                    SecurityEventType.LOGOUT,
                    ipAddress,
                    sessionId,
                    "Logout all sessions succeeded."
            ));
            return;
        }

        refreshTokenStore.revokeFamily(sessionId);

        auditLogger.log(SecurityAuditEvent.success(
                userId,
                SecurityEventType.LOGOUT,
                ipAddress,
                sessionId,
                "Logout current session succeeded."
        ));
    }
}