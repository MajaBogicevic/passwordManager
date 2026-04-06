package org.service.passwordman.application.service.vault;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.vault.LockVaultUseCase;
import org.service.passwordman.domain.exception.ValidationException;

public class LockVaultService implements LockVaultUseCase {

    private final VaultSessionStore vaultSessionStore;
    private final AuditLogger auditLogger;

    public LockVaultService(
            VaultSessionStore vaultSessionStore,
            AuditLogger auditLogger
    ) {
        this.vaultSessionStore = vaultSessionStore;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(int userId) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        vaultSessionStore.lock(userId);
        auditLogger.log(userId, "VAULT_LOCKED", "localhost");
    }
}