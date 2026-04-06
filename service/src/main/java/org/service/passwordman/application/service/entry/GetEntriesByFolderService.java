package org.service.passwordman.application.service.entry;

import java.util.List;

import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.GetEntriesByFolderUseCase;
import org.service.passwordman.domain.exception.FolderNotFoundException;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.Folder;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class GetEntriesByFolderService implements GetEntriesByFolderUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final VaultSessionStore vaultSessionStore;

    public GetEntriesByFolderService(
            PasswordEntryRepository passwordEntryRepository,
            FolderRepository folderRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.vaultSessionStore = vaultSessionStore;
    }

    @Override
    public List<PasswordEntry> execute(int userId, int folderId, String jwtTokenId) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultSessionExpiredException();
        }

        folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new FolderNotFoundException(String.valueOf(folderId)));

        return passwordEntryRepository.findByFolderIdAndUserId(folderId, userId);
    }
}