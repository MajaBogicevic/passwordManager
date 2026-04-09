package org.service.passwordman.application.service.folder;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.usecase.folder.RenameFolderUseCase;
import org.service.passwordman.domain.exception.FolderNotFoundException;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.model.Folder;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class RenameFolderService implements RenameFolderUseCase {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final AuditLogger auditLogger;

    public RenameFolderService(
            FolderRepository folderRepository,
            UserRepository userRepository,
            AuditLogger auditLogger
    ) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(int userId, int folderId, String newName, String ipAddress) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        Folder folder = folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new FolderNotFoundException(String.valueOf(folderId)));

        if (folder.getUserId() != userId) {
            throw new UnauthorizedVaultAccessException();
        }

        Folder renamedFolder = new Folder(
                folder.getId(),
                folder.getUserId(),
                newName
        );

        folderRepository.save(renamedFolder);
        auditLogger.log(userId, "FOLDER_RENAMED", ipAddress);
    }
}