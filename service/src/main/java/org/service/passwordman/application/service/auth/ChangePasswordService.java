package org.service.passwordman.application.service.auth;

import java.time.ZoneId;
import java.util.Arrays;

import org.crypt.crypto.kdf.DataKeyWrapper;
import org.crypt.crypto.kdf.Pbkdf2KeyDerivation;
import org.crypt.crypto.util.Base64Url;
import org.crypt.crypto.util.ZeroUtils;
import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.RefreshTokenStore;
import org.service.passwordman.application.port.UserAuthInvalidationStore;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.usecase.auth.ChangePasswordUseCase;
import org.service.passwordman.domain.exception.InvalidCredentialsException;
import org.service.passwordman.domain.exception.UserNotFoundException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.SecurityEventType;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;


public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final VaultSessionStore vaultSessionStore;
    private final VaultKeyStore vaultKeyStore;
    private final AuditLogger auditLogger;
    private final Clock clock;
    private final RefreshTokenStore refreshTokenStore;
    private final UserAuthInvalidationStore userAuthInvalidationStore;

    public ChangePasswordService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            AuditLogger auditLogger,
            Clock clock,
            RefreshTokenStore refreshTokenStore,
            UserAuthInvalidationStore userAuthInvalidationStore
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.vaultSessionStore = vaultSessionStore;
        this.vaultKeyStore = vaultKeyStore;
        this.auditLogger = auditLogger;
        this.clock = clock;
        this.refreshTokenStore = refreshTokenStore;
        this.userAuthInvalidationStore = userAuthInvalidationStore;
    }

    @Override
    public void execute(int userId, String jwtTokenId, String oldPassword, String newPassword, String ipAddress) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new ValidationException("Old password is required.");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new ValidationException("New password is required.");
        }

        if (oldPassword.equals(newPassword)) {
            throw new ValidationException("New password must be different from the old password.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordHasher.matches(oldPassword, user.getPasswordHash())) {
            auditLogger.log(SecurityAuditEvent.failure(
                    userId,
                    SecurityEventType.PASSWORD_CHANGED,
                    "INVALID_OLD_PASSWORD",
                    ipAddress,
                    jwtTokenId,
                    "Password change failed because old password did not match."
            ));
            throw new InvalidCredentialsException();
        }

        char[] oldPasswordChars = oldPassword.toCharArray();
        char[] newPasswordChars = newPassword.toCharArray();
        byte[] oldSalt = Base64Url.decode(user.getKeySalt());
        byte[] oldKek = null;
        byte[] dataEncryptionKey = null;
        byte[] newKek = null;
        String newWrappedDataKey;
        byte[] newSalt;

        try {
            oldKek = Pbkdf2KeyDerivation.deriveKey(oldPasswordChars, oldSalt);
            dataEncryptionKey = DataKeyWrapper.unwrap(user.getWrappedDataKey(), oldKek);

            newSalt = Pbkdf2KeyDerivation.generateSalt();
            newKek = Pbkdf2KeyDerivation.deriveKey(newPasswordChars, newSalt);
            newWrappedDataKey = DataKeyWrapper.wrap(dataEncryptionKey, newKek);
        } finally {
            Arrays.fill(oldPasswordChars, '\0');
            Arrays.fill(newPasswordChars, '\0');
            ZeroUtils.zero(oldKek);
            ZeroUtils.zero(newKek);
            ZeroUtils.zero(dataEncryptionKey);
        }

        String newPasswordHash = passwordHasher.hash(newPassword);

        User updatedUser = new User(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                newPasswordHash,
                Base64Url.encode(newSalt),
                newWrappedDataKey,
                user.getNotes(),
                user.getCreatedAt(),
                clock.now()
        );

        userRepository.save(updatedUser);

        vaultSessionStore.lockAllForUser(userId);
        vaultKeyStore.clearAllForUser(userId);
        refreshTokenStore.revokeAllByUserId(userId);

        long cutoffMillis = clock.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        userAuthInvalidationStore.invalidateAllTokensForUser(userId, cutoffMillis);

        auditLogger.log(SecurityAuditEvent.success(
                userId,
                SecurityEventType.PASSWORD_CHANGED,
                ipAddress,
                jwtTokenId,
                "Password changed successfully. Sessions invalidated and vault locked."
        ));
    }
}