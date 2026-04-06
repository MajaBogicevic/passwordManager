package org.service.passwordman.application.usecase.folder;

public interface CreateFolderUseCase {
    void execute(int userId, String folderName);
}