package org.service.passwordman.application.service.vault;

import java.time.Duration;
import java.time.LocalDateTime;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.domain.exception.VaultLockedException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;

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
    public void execute(int userId) {
        lockIfExpired(userId);
    }

    @Override
    public void ensureVaultIsActive(int userId) {
        if (!vaultSessionStore.isUnlocked(userId)) {
            throw new VaultLockedException();
        }

        LocalDateTime lastActivityAt = vaultSessionStore.getLastActivityAt(userId)
                .orElseThrow(VaultLockedException::new);

        LocalDateTime now = clock.now();
        Duration inactiveFor = Duration.between(lastActivityAt, now);

        if (inactiveFor.compareTo(timeout) >= 0) {
            vaultSessionStore.lock(userId);
            auditLogger.log(userId, "VAULT_AUTO_LOCKED", "localhost");
            throw new VaultSessionExpiredException();
        }
    }

    @Override
    public void refreshActivity(int userId) {
        vaultSessionStore.refreshActivity(userId, clock.now());
    }

    private void lockIfExpired(int userId) {
        if (!vaultSessionStore.isUnlocked(userId)) {
            return;
        }

        LocalDateTime lastActivityAt = vaultSessionStore.getLastActivityAt(userId)
                .orElse(null);

        if (lastActivityAt == null) {
            vaultSessionStore.lock(userId);
            return;
        }

        LocalDateTime now = clock.now();
        Duration inactiveFor = Duration.between(lastActivityAt, now);

        if (inactiveFor.compareTo(timeout) >= 0) {
            vaultSessionStore.lock(userId);
            auditLogger.log(userId, "VAULT_AUTO_LOCKED", "localhost");
        }
    }
}