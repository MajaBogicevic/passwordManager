package org.service.passwordman.application.usecase.folder;

public interface DeleteFolderUseCase {
    void execute(int userId, int folderId, String ipAddress);
}