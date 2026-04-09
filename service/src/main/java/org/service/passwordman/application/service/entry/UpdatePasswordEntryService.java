package org.service.passwordman.application.service.entry;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.UpdatePasswordEntryUseCase;
import org.service.passwordman.domain.exception.EntryNotFoundException;
import org.service.passwordman.domain.exception.FolderNotFoundException;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class UpdatePasswordEntryService implements UpdatePasswordEntryUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final UserRepository userRepository;
    private final VaultSessionStore vaultSessionStore;
    private final EncryptionService encryptionService;
    private final Clock clock;
    private final AuditLogger auditLogger;
    private final FolderRepository folderRepository;

    public UpdatePasswordEntryService(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            EncryptionService encryptionService,
            Clock clock,
            AuditLogger auditLogger,
            FolderRepository folderRepository
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.userRepository = userRepository;
        this.vaultSessionStore = vaultSessionStore;
        this.encryptionService = encryptionService;
        this.clock = clock;
        this.auditLogger = auditLogger;
        this.folderRepository = folderRepository;
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
            int folderId,
            String jwtTokenId,
            String ipAddress
    ) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultSessionExpiredException();
        }

        PasswordEntry entry = passwordEntryRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new EntryNotFoundException(String.valueOf(entryId)));

        if (folderId > 0) {
            folderRepository.findByIdAndUserId(folderId, userId)
                    .orElseThrow(() -> new FolderNotFoundException(String.valueOf(folderId)));
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
        auditLogger.log(userId, "PASSWORD_ENTRY_UPDATED", ipAddress);
    }
}