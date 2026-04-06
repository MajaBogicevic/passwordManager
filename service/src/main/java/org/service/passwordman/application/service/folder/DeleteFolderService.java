package org.service.passwordman.application.service.folder;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.usecase.folder.DeleteFolderUseCase;
import org.service.passwordman.domain.exception.FolderNotFoundException;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.model.Folder;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class DeleteFolderService implements DeleteFolderUseCase {

    private final FolderRepository folderRepository;
    private final PasswordEntryRepository passwordEntryRepository;
    private final UserRepository userRepository;
    private final AuditLogger auditLogger;

    public DeleteFolderService(
            FolderRepository folderRepository,
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            AuditLogger auditLogger
    ) {
        this.folderRepository = folderRepository;
        this.passwordEntryRepository = passwordEntryRepository;
        this.userRepository = userRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(int userId, int folderId) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(String.valueOf(folderId)));

        if (folder.getUserId() != userId) {
            throw new UnauthorizedVaultAccessException();
        }

        boolean folderHasEntries = !passwordEntryRepository.findByFolderId(folderId).isEmpty();
        if (folderHasEntries) {
            throw new IllegalStateException("Folder cannot be deleted because it still contains password entries.");
        }

        folderRepository.deleteById(folderId);
        auditLogger.log(userId, "folder_deleted", folder.getName());
    }
}