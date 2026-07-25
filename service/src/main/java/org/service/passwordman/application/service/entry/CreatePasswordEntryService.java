package org.service.passwordman.application.service.entry;

import java.time.LocalDateTime;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.CreatePasswordEntryUseCase;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.VaultLockedException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class CreatePasswordEntryService implements CreatePasswordEntryUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final UserRepository userRepository;
    private final VaultSessionStore vaultSessionStore;
    private final VaultKeyStore vaultKeyStore;
    private final EncryptionService encryptionService;
    private final Clock clock;
    private final AuditLogger auditLogger;
    private final FolderRepository folderRepository;

    public CreatePasswordEntryService(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService,
            Clock clock,
            AuditLogger auditLogger,
            FolderRepository folderRepository
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.userRepository = userRepository;
        this.vaultSessionStore = vaultSessionStore;
        this.vaultKeyStore = vaultKeyStore;
        this.encryptionService = encryptionService;
        this.clock = clock;
        this.auditLogger = auditLogger;
        this.folderRepository = folderRepository;

    }

    @Override
    public void execute(
            int userId,
            String title,
            String url,
            String username,
            String plaintextPassword,
            String notes,
            int folderId,
            String ip,
            String jwtTokenId
    ) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultSessionExpiredException();
        }

        byte[] dataEncryptionKey = vaultKeyStore.get(userId, jwtTokenId)
                .orElseThrow(VaultLockedException::new);

        if (folderId > 0) {
            folderRepository.findByIdAndUserId(folderId, userId)
                    .orElseThrow(() -> new UnauthorizedVaultAccessException());
        }

        String encryptedPassword = encryptionService.encrypt(dataEncryptionKey, plaintextPassword);
        String encryptedUsername = encryptionService.encrypt(dataEncryptionKey, username);
        String encryptedNotes = notes == null ? null : encryptionService.encrypt(dataEncryptionKey, notes);
        LocalDateTime now = clock.now();

        PasswordEntry passwordEntry = new PasswordEntry(
                0,
                userId,
                title,
                url,
                encryptedUsername,
                encryptedPassword,
                encryptedNotes,
                folderId,
                now,
                now
        );

        passwordEntryRepository.save(passwordEntry);
        auditLogger.log(userId, "PASSWORD_ENTRY_CREATED", ip);
    }
}