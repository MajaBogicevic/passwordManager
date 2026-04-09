package org.service.passwordman.application.service.vault;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.usecase.vault.LockVaultUseCase;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.SecurityEventType;

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
    public void execute(int userId, String jwtTokenId, String ipAddress) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (jwtTokenId == null || jwtTokenId.isBlank()) {
            throw new ValidationException("JWT token id is required.");
        }

        vaultSessionStore.lock(userId, jwtTokenId);

        auditLogger.log(SecurityAuditEvent.success(
                userId,
                SecurityEventType.VAULT_LOCK,
                ipAddress,
                jwtTokenId,
                "Vault locked by user action."
        ));
    }
}