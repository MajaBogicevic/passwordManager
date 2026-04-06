package org.service.passwordman.application.usecase.folder;

import org.service.passwordman.domain.model.Folder;

public interface GetFolderUseCase {
    Folder execute(int folderId);
}