package org.service.passwordman.application.usecase.folder;

import org.service.passwordman.domain.model.Folder;

public interface CreateFolderUseCase {
    Folder execute(int userId, String folderName, String ipAddress);
}