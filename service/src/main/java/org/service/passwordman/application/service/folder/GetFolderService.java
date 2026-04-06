package org.service.passwordman.application.service.folder;

import org.service.passwordman.application.usecase.folder.GetFolderUseCase;
import org.service.passwordman.domain.exception.FolderNotFoundException;
import org.service.passwordman.domain.model.Folder;
import org.service.passwordman.domain.repository.FolderRepository;

public class GetFolderService implements GetFolderUseCase {

    private final FolderRepository folderRepository;

    public GetFolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Override
    public Folder execute(int userId, int folderId) {
        return folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() ->
                        new FolderNotFoundException("Folder with id " + folderId + " was not found."));
    }
}