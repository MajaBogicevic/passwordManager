package org.service.passwordman.application.service.entry;

import java.time.LocalDateTime;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.CreatePasswordEntryUseCase;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class CreatePasswordEntryService implements CreatePasswordEntryUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final UserRepository userRepository;
    private final VaultSessionStore vaultSessionStore;
    private final EncryptionService encryptionService;
    private final Clock clock;
    private final AuditLogger auditLogger;

    public CreatePasswordEntryService(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            EncryptionService encryptionService,
            Clock clock,
            AuditLogger auditLogger
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.userRepository = userRepository;
        this.vaultSessionStore = vaultSessionStore;
        this.encryptionService = encryptionService;
        this.clock = clock;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(
            int userId,
            String title,
            String url,
            String username,
            String plaintextPassword,
            String notes,
            int folderId
    ) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (!vaultSessionStore.isUnlocked(userId)) {
            throw new VaultSessionExpiredException();
        }

        String encryptedPassword = encryptionService.encrypt(plaintextPassword);
        LocalDateTime now = clock.now();

        PasswordEntry passwordEntry = new PasswordEntry(
                0,
                userId,
                title,
                url,
                username,
                encryptedPassword,
                notes,
                folderId,
                now,
                now
        );

        passwordEntryRepository.save(passwordEntry);
        auditLogger.log(userId, "password_entry_created", title);
    }
}