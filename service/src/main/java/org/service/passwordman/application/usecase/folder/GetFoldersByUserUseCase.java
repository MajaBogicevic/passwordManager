package org.service.passwordman.application.usecase.folder;

import org.service.passwordman.domain.model.Folder;

import java.util.List;

public interface GetFoldersByUserUseCase {
    List<Folder> execute(int userId);
}