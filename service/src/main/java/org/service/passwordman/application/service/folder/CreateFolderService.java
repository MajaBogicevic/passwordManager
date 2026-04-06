package org.service.passwordman.application.service.folder;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.usecase.folder.CreateFolderUseCase;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.model.Folder;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class CreateFolderService implements CreateFolderUseCase {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final AuditLogger auditLogger;

    public CreateFolderService(
            FolderRepository folderRepository,
            UserRepository userRepository,
            AuditLogger auditLogger
    ) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(int userId, String folderName) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        Folder folder = new Folder(0, userId, folderName);
        folderRepository.save(folder);

        auditLogger.log(userId, "FOLDER_CREATED", null);
    }
}