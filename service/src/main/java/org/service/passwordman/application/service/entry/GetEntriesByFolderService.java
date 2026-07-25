package org.service.passwordman.application.service.entry;

import java.util.List;

import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.GetEntriesByFolderUseCase;
import org.service.passwordman.domain.exception.FolderNotFoundException;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.VaultLockedException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class GetEntriesByFolderService implements GetEntriesByFolderUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final VaultSessionStore vaultSessionStore;
    private final VaultKeyStore vaultKeyStore;
    private final EncryptionService encryptionService;

    public GetEntriesByFolderService(
            PasswordEntryRepository passwordEntryRepository,
            FolderRepository folderRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.vaultSessionStore = vaultSessionStore;
        this.vaultKeyStore = vaultKeyStore;
        this.encryptionService = encryptionService;
    }

    @Override
    public List<PasswordEntry> execute(int userId, int folderId, String jwtTokenId) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultSessionExpiredException();
        }

        byte[] dataEncryptionKey = vaultKeyStore.get(userId, jwtTokenId)
                .orElseThrow(VaultLockedException::new);

        folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new FolderNotFoundException(String.valueOf(folderId)));

        List<PasswordEntry> entries = passwordEntryRepository.findByFolderIdAndUserId(folderId, userId);
        return PasswordEntryDecryptor.decryptMetadata(entries, dataEncryptionKey, encryptionService);
    }
}