package org.service.passwordman.application.service.auth;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.RateLimitStore;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.security.TokenPayload;
import org.service.passwordman.application.usecase.auth.LoginUserUseCase;
import org.service.passwordman.domain.exception.InvalidCredentialsException;
import org.service.passwordman.domain.exception.TooManyRequestsException;
import org.service.passwordman.domain.model.SecurityEventType;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;

public class LoginUserService implements LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AuditLogger auditLogger;
    private final RateLimitStore rateLimitStore;
    private final int maxAttempts;
    private final long blockDurationMillis;

    public LoginUserService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AuditLogger auditLogger,
            RateLimitStore rateLimitStore,
            int maxAttempts,
            long blockDurationMillis
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.auditLogger = auditLogger;
        this.rateLimitStore = rateLimitStore;
        this.maxAttempts = maxAttempts;
        this.blockDurationMillis = blockDurationMillis;
    }

    @Override
    public TokenPayload execute(String username, String password, String ip) {
        String rateLimitKey = buildRateLimitKey(username, ip);

        if (rateLimitStore.isBlocked(rateLimitKey)) {
            auditLogger.log(SecurityAuditEvent.rateLimited(
                    0,
                    SecurityEventType.LOGIN,
                    ip,
                    null,
                    "Login rate limit triggered."
            ));
            throw new TooManyRequestsException("Too many failed login attempts. Please try again later.");
        }

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            rateLimitStore.recordFailure(rateLimitKey, maxAttempts, blockDurationMillis);
            auditLogger.log(SecurityAuditEvent.failure(
                    0,
                    SecurityEventType.LOGIN,
                    "INVALID_CREDENTIALS",
                    ip,
                    null,
                    "Login failed for unknown username."
            ));
            throw new InvalidCredentialsException();
        }

        boolean matches = passwordHasher.matches(password, user.getPasswordHash());

        if (!matches) {
            rateLimitStore.recordFailure(rateLimitKey, maxAttempts, blockDurationMillis);
            auditLogger.log(SecurityAuditEvent.failure(
                    user.getId(),
                    SecurityEventType.LOGIN,
                    "INVALID_CREDENTIALS",
                    ip,
                    null,
                    "Login failed because password did not match."
            ));
            throw new InvalidCredentialsException();
        }

        rateLimitStore.reset(rateLimitKey);
        auditLogger.log(SecurityAuditEvent.success(
                user.getId(),
                SecurityEventType.LOGIN,
                ip,
                null,
                "Login succeeded."
        ));

        return new TokenPayload(
                user.getId(),
                user.getUsername(),
                null,
                null,
                "access"
        );
    }

    private String buildRateLimitKey(String username, String ip) {
        String normalizedUsername = username == null ? "unknown" : username.trim().toLowerCase();
        String normalizedIp = ip == null || ip.isBlank() ? "unknown-ip" : ip.trim();
        return "login:" + normalizedUsername + ":" + normalizedIp;
    }
}