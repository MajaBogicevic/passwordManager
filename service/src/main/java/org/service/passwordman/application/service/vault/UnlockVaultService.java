package org.service.passwordman.application.service.vault;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.vault.UnlockVaultUseCase;
import org.service.passwordman.domain.exception.InvalidCredentialsException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;

public class UnlockVaultService implements UnlockVaultUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final VaultSessionStore vaultSessionStore;
    private final AuditLogger auditLogger;
    private final Clock clock;

    public UnlockVaultService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            VaultSessionStore vaultSessionStore,
            AuditLogger auditLogger,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.vaultSessionStore = vaultSessionStore;
        this.auditLogger = auditLogger;
        this.clock = clock;
    }

    @Override
    public void execute(int userId, String masterPassword) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (masterPassword == null || masterPassword.trim().isEmpty()) {
            throw new ValidationException("Master password is required.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found."));

        if (!passwordHasher.matches(masterPassword, user.getMasterPasswordHash())) {
            auditLogger.log(userId, "VAULT_UNLOCK_FAILED", "localhost");
            throw new InvalidCredentialsException();
        }

        vaultSessionStore.unlock(userId, clock.now());
        auditLogger.log(userId, "VAULT_UNLOCK_SUCCESS", "localhost");
    }
}