package org.service.passwordman.application.service.vault;

import java.util.Arrays;

import org.crypt.crypto.kdf.DataKeyWrapper;
import org.crypt.crypto.kdf.Pbkdf2KeyDerivation;
import org.crypt.crypto.util.Base64Url;
import org.crypt.crypto.util.ZeroUtils;
import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.RateLimitStore;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.usecase.vault.UnlockVaultUseCase;
import org.service.passwordman.domain.exception.InvalidCredentialsException;
import org.service.passwordman.domain.exception.TooManyRequestsException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.SecurityEventType;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;

public class UnlockVaultService implements UnlockVaultUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final VaultSessionStore vaultSessionStore;
    private final VaultKeyStore vaultKeyStore;
    private final AuditLogger auditLogger;
    private final Clock clock;
    private final RateLimitStore rateLimitStore;
    private final int maxAttempts;
    private final long blockDurationMillis;

    public UnlockVaultService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            AuditLogger auditLogger,
            Clock clock,
            RateLimitStore rateLimitStore,
            int maxAttempts,
            long blockDurationMillis
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.vaultSessionStore = vaultSessionStore;
        this.vaultKeyStore = vaultKeyStore;
        this.auditLogger = auditLogger;
        this.clock = clock;
        this.rateLimitStore = rateLimitStore;
        this.maxAttempts = maxAttempts;
        this.blockDurationMillis = blockDurationMillis;
    }

    @Override
    public void execute(int userId, String jwtTokenId, String masterPassword, String ipAddress) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (jwtTokenId == null || jwtTokenId.isBlank()) {
            throw new ValidationException("JWT token id is required.");
        }

        if (masterPassword == null || masterPassword.trim().isEmpty()) {
            throw new ValidationException("Master password is required.");
        }

        String rateLimitKey = buildRateLimitKey(userId, jwtTokenId, ipAddress);

        if (rateLimitStore.isBlocked(rateLimitKey)) {
            auditLogger.log(SecurityAuditEvent.rateLimited(
                    userId,
                    SecurityEventType.VAULT_UNLOCK,
                    ipAddress,
                    jwtTokenId,
                    "Vault unlock rate limit triggered."
            ));
            throw new TooManyRequestsException("Too many failed vault unlock attempts. Please try again later.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found."));

        if (!passwordHasher.matches(masterPassword, user.getPasswordHash())) {
            rateLimitStore.recordFailure(rateLimitKey, maxAttempts, blockDurationMillis);
            auditLogger.log(SecurityAuditEvent.failure(
                    userId,
                    SecurityEventType.VAULT_UNLOCK,
                    "INVALID_MASTER_PASSWORD",
                    ipAddress,
                    jwtTokenId,
                    "Vault unlock failed because master password did not match."
            ));
            throw new InvalidCredentialsException();
        }

        char[] passwordChars = masterPassword.toCharArray();
        byte[] salt = Base64Url.decode(user.getKeySalt());
        byte[] keyEncryptionKey = null;
        byte[] dataEncryptionKey = null;

        try {
            keyEncryptionKey = Pbkdf2KeyDerivation.deriveKey(passwordChars, salt);
            dataEncryptionKey = DataKeyWrapper.unwrap(user.getWrappedDataKey(), keyEncryptionKey);
            vaultKeyStore.store(userId, jwtTokenId, dataEncryptionKey);
        } finally {
            Arrays.fill(passwordChars, '\0');
            ZeroUtils.zero(keyEncryptionKey);
            ZeroUtils.zero(dataEncryptionKey);
        }

        rateLimitStore.reset(rateLimitKey);
        vaultSessionStore.unlock(userId, jwtTokenId, clock.now());

        auditLogger.log(SecurityAuditEvent.success(
                userId,
                SecurityEventType.VAULT_UNLOCK,
                ipAddress,
                jwtTokenId,
                "Vault unlock succeeded."
        ));
    }

    private String buildRateLimitKey(int userId, String jwtTokenId, String ipAddress) {
        String normalizedIp = ipAddress == null || ipAddress.isBlank() ? "unknown-ip" : ipAddress.trim();
        return "vault-unlock:" + userId + ":" + jwtTokenId + ":" + normalizedIp;
    }
}