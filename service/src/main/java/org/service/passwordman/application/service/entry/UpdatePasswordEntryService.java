package org.service.passwordman.application.service.entry;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.UpdatePasswordEntryUseCase;
import org.service.passwordman.domain.exception.EntryNotFoundException;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class UpdatePasswordEntryService implements UpdatePasswordEntryUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final UserRepository userRepository;
    private final VaultSessionStore vaultSessionStore;
    private final EncryptionService encryptionService;
    private final Clock clock;
    private final AuditLogger auditLogger;

    public UpdatePasswordEntryService(
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
            int entryId,
            String title,
            String url,
            String username,
            String plainPassword,
            String notes,
            int folderId
    ) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (!vaultSessionStore.isUnlocked(userId)) {
            throw new VaultSessionExpiredException();
        }

        PasswordEntry entry = passwordEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntryNotFoundException(String.valueOf(entryId)));

        if (entry.getUserId() != userId) {
            throw new UnauthorizedVaultAccessException();
        }

        String encryptedPassword = encryptionService.encrypt(plainPassword);

        entry.update(
                title,
                url,
                username,
                encryptedPassword,
                notes,
                folderId,
                clock.now()
        );

        passwordEntryRepository.save(entry);
        auditLogger.log(userId, "PASSWORD_ENTRY_UPDATED", null);
    }
}