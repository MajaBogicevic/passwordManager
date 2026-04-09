package org.service.passwordman.application.service.auth;

import java.time.ZoneId;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.RefreshTokenStore;
import org.service.passwordman.application.port.UserAuthInvalidationStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.usecase.auth.ChangeLoginPasswordUseCase;
import org.service.passwordman.domain.exception.InvalidCredentialsException;
import org.service.passwordman.domain.exception.UserNotFoundException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.SecurityEventType;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;

public class ChangeLoginPasswordService implements ChangeLoginPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AuditLogger auditLogger;
    private final Clock clock;
    private final RefreshTokenStore refreshTokenStore;
    private final VaultSessionStore vaultSessionStore;
    private final UserAuthInvalidationStore userAuthInvalidationStore;

    public ChangeLoginPasswordService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AuditLogger auditLogger,
            Clock clock,
            RefreshTokenStore refreshTokenStore,
            VaultSessionStore vaultSessionStore,
            UserAuthInvalidationStore userAuthInvalidationStore
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.auditLogger = auditLogger;
        this.clock = clock;
        this.refreshTokenStore = refreshTokenStore;
        this.vaultSessionStore = vaultSessionStore;
        this.userAuthInvalidationStore = userAuthInvalidationStore;
    }

    @Override
    public void execute(int userId, String oldLoginPassword, String newLoginPassword, String ipAddress) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (oldLoginPassword == null || oldLoginPassword.trim().isEmpty()) {
            throw new ValidationException("Old login password is required.");
        }

        if (newLoginPassword == null || newLoginPassword.trim().isEmpty()) {
            throw new ValidationException("New login password is required.");
        }

        if (oldLoginPassword.equals(newLoginPassword)) {
            throw new ValidationException("New login password must be different from the old login password.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordHasher.matches(oldLoginPassword, user.getLoginPasswordHash())) {
            auditLogger.log(SecurityAuditEvent.failure(
                    userId,
                    SecurityEventType.LOGIN_PASSWORD_CHANGED,
                    "INVALID_OLD_LOGIN_PASSWORD",
                    ipAddress,
                    null,
                    "Login password change failed because old password did not match."
            ));
            throw new InvalidCredentialsException();
        }

        String newHash = passwordHasher.hash(newLoginPassword);

        User updatedUser = new User(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                newHash,
                user.getMasterPasswordHash(),
                user.getNotes(),
                user.getCreatedAt(),
                clock.now()
        );

        userRepository.save(updatedUser);

        refreshTokenStore.revokeAllByUserId(userId);
        vaultSessionStore.lockAllForUser(userId);

        long cutoffMillis = clock.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        userAuthInvalidationStore.invalidateAllTokensForUser(userId, cutoffMillis);

        auditLogger.log(SecurityAuditEvent.success(
                userId,
                SecurityEventType.LOGIN_PASSWORD_CHANGED,
                ipAddress,
                null,
                "Login password changed successfully. Sessions invalidated."
        ));
    }
}