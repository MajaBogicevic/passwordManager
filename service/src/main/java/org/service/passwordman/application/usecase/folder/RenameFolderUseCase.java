package org.service.passwordman.application.usecase.folder;

public interface RenameFolderUseCase {
    void execute(int userId, int folderId, String newName, String ipAddress);
}