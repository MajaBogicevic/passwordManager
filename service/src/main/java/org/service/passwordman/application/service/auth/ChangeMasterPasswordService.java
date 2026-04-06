package org.service.passwordman.application.service.auth;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.auth.ChangeMasterPasswordUseCase;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;

public class ChangeMasterPasswordService implements ChangeMasterPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final VaultSessionStore vaultSessionStore;
    private final AuditLogger auditLogger;
    private final Clock clock;

    public ChangeMasterPasswordService(
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
    public void execute(int userId, String oldMasterPassword, String newMasterPassword, String ipAddress) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (oldMasterPassword == null || oldMasterPassword.trim().isEmpty()) {
            throw new ValidationException("Old master password is required.");
        }

        if (newMasterPassword == null || newMasterPassword.trim().isEmpty()) {
            throw new ValidationException("New master password is required.");
        }

        if (!vaultSessionStore.isUnlocked(userId)) {
            throw new VaultSessionExpiredException();
        }

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            auditLogger.log(userId, "CHANGE_MASTER_PASSWORD_FAILED", ipAddress);
            throw new ValidationException("User not found.");
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

        auditLogger.log(userId, "CHANGE_MASTER_PASSWORD_SUCCESS", "localhost");
    }
}