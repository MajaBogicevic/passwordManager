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
    public void execute(int userId, int folderId, String ipAddress) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        Folder folder = folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new FolderNotFoundException(String.valueOf(folderId)));

        boolean deleted = folderRepository.deleteByIdAndUserId(folder.getId(), userId);
        if (!deleted) {
            throw new FolderNotFoundException(String.valueOf(folderId));
        }

        auditLogger.log(userId, "FOLDER_DELETED", ipAddress);
    }
}