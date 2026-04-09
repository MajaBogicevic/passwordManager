package org.service.passwordman.application.service.vault;

import java.time.Duration;
import java.time.LocalDateTime;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.exception.VaultLockedException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.SecurityEventType;

public class AutoLockService implements AutoLockUseCase {

    private final VaultSessionStore vaultSessionStore;
    private final Clock clock;
    private final AuditLogger auditLogger;
    private final Duration timeout;

    public AutoLockService(
            VaultSessionStore vaultSessionStore,
            Clock clock,
            AuditLogger auditLogger,
            Duration timeout
    ) {
        this.vaultSessionStore = vaultSessionStore;
        this.clock = clock;
        this.auditLogger = auditLogger;
        this.timeout = timeout;
    }

    @Override
    public void execute(int userId, String jwtTokenId, String ipAddress) {
        lockIfExpired(userId, jwtTokenId, ipAddress);
    }

    @Override
    public void ensureVaultIsActive(int userId, String jwtTokenId, String ipAddress) {
        validate(userId, jwtTokenId);

        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultLockedException();
        }

        LocalDateTime lastActivityAt = vaultSessionStore.getLastActivityAt(userId, jwtTokenId)
                .orElseThrow(VaultLockedException::new);

        Duration inactiveFor = Duration.between(lastActivityAt, clock.now());

        if (inactiveFor.compareTo(timeout) >= 0) {
            vaultSessionStore.lock(userId, jwtTokenId);
            auditLogger.log(SecurityAuditEvent.success(
                    userId,
                    SecurityEventType.VAULT_AUTO_LOCK,
                    ipAddress,
                    jwtTokenId,
                    "Vault auto-locked because inactivity timeout was reached."
            ));
            throw new VaultSessionExpiredException();
        }
    }

    @Override
    public void refreshActivity(int userId, String jwtTokenId) {
        validate(userId, jwtTokenId);
        vaultSessionStore.refreshActivity(userId, jwtTokenId, clock.now());
    }

    private void lockIfExpired(int userId, String jwtTokenId, String ipAddress) {
        validate(userId, jwtTokenId);

        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            return;
        }

        LocalDateTime lastActivityAt = vaultSessionStore.getLastActivityAt(userId, jwtTokenId)
                .orElse(null);

        if (lastActivityAt == null) {
            vaultSessionStore.lock(userId, jwtTokenId);
            auditLogger.log(SecurityAuditEvent.success(
                    userId,
                    SecurityEventType.VAULT_AUTO_LOCK,
                    ipAddress,
                    jwtTokenId,
                    "Vault auto-locked because last activity was missing."
            ));
            return;
        }

        Duration inactiveFor = Duration.between(lastActivityAt, clock.now());

        if (inactiveFor.compareTo(timeout) >= 0) {
            vaultSessionStore.lock(userId, jwtTokenId);
            auditLogger.log(SecurityAuditEvent.success(
                    userId,
                    SecurityEventType.VAULT_AUTO_LOCK,
                    ipAddress,
                    jwtTokenId,
                    "Vault auto-locked because inactivity timeout was reached."
            ));
        }
    }

    private void validate(int userId, String jwtTokenId) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (jwtTokenId == null || jwtTokenId.isBlank()) {
            throw new ValidationException("JWT token id is required.");
        }
    }
}