package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.FolderHandler;
import org.service.passwordman.desktopApi.request.CreateFolderRequest;
import org.service.passwordman.desktopApi.request.RenameFolderRequest;

public class FolderController {

    private final FolderHandler folderHandler;

    public FolderController(FolderHandler folderHandler) {
        this.folderHandler = folderHandler;
    }

    public Object create(CreateFolderRequest request) {
        return folderHandler.createSafe(request);
    }

    public Object get(int folderId) {
        return folderHandler.getSafe(folderId);
    }

    public Object getByUser(int userId) {
        return folderHandler.getByUserSafe(userId);
    }

    public Object rename(RenameFolderRequest request) {
        return folderHandler.renameSafe(request);
    }

    public Object delete(int userId, int folderId) {
        return folderHandler.deleteSafe(userId, folderId);
    }
}