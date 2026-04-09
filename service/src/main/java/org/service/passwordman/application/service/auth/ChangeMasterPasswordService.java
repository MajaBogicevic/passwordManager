package org.service.passwordman.application.service.auth;

import java.time.ZoneId;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.RefreshTokenStore;
import org.service.passwordman.application.port.UserAuthInvalidationStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.usecase.auth.ChangeMasterPasswordUseCase;
import org.service.passwordman.domain.exception.InvalidCredentialsException;
import org.service.passwordman.domain.exception.UserNotFoundException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.SecurityEventType;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;

public class ChangeMasterPasswordService implements ChangeMasterPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final VaultSessionStore vaultSessionStore;
    private final AuditLogger auditLogger;
    private final Clock clock;
    private final RefreshTokenStore refreshTokenStore;
    private final UserAuthInvalidationStore userAuthInvalidationStore;

    public ChangeMasterPasswordService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            VaultSessionStore vaultSessionStore,
            AuditLogger auditLogger,
            Clock clock,
            RefreshTokenStore refreshTokenStore,
            UserAuthInvalidationStore userAuthInvalidationStore
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.vaultSessionStore = vaultSessionStore;
        this.auditLogger = auditLogger;
        this.clock = clock;
        this.refreshTokenStore = refreshTokenStore;
        this.userAuthInvalidationStore = userAuthInvalidationStore;
    }

    @Override
    public void execute(int userId, String jwtTokenId, String oldMasterPassword, String newMasterPassword, String ipAddress) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (oldMasterPassword == null || oldMasterPassword.trim().isEmpty()) {
            throw new ValidationException("Old master password is required.");
        }

        if (newMasterPassword == null || newMasterPassword.trim().isEmpty()) {
            throw new ValidationException("New master password is required.");
        }

        if (oldMasterPassword.equals(newMasterPassword)) {
            throw new ValidationException("New master password must be different from the old master password.");
        }

        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultSessionExpiredException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordHasher.matches(oldMasterPassword, user.getMasterPasswordHash())) {
            auditLogger.log(SecurityAuditEvent.failure(
                    userId,
                    SecurityEventType.MASTER_PASSWORD_CHANGED,
                    "INVALID_OLD_MASTER_PASSWORD",
                    ipAddress,
                    null,
                    "Master password change failed because old password did not match."
            ));
            throw new InvalidCredentialsException();
        }

        String newHash = passwordHasher.hash(newMasterPassword);

        User updatedUser = new User(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getLoginPasswordHash(),
                newHash,
                user.getNotes(),
                user.getCreatedAt(),
                clock.now()
        );

        userRepository.save(updatedUser);

        vaultSessionStore.lockAllForUser(userId);
        refreshTokenStore.revokeAllByUserId(userId);

        long cutoffMillis = clock.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        userAuthInvalidationStore.invalidateAllTokensForUser(userId, cutoffMillis);

        auditLogger.log(SecurityAuditEvent.success(
                userId,
                SecurityEventType.MASTER_PASSWORD_CHANGED,
                ipAddress,
                null,
                "Master password changed successfully. Sessions invalidated and vault locked."
        ));
    }
}