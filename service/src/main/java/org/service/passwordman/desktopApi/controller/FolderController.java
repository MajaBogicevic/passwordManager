package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.FolderHandler;
import org.service.passwordman.desktopApi.request.CreateFolderRequest;
import org.service.passwordman.desktopApi.request.RenameFolderRequest;
import org.service.passwordman.infrastructure.security.ClientIp;

import jakarta.servlet.http.HttpServletRequest;

public class FolderController {

    private final FolderHandler folderHandler;
    private final ClientIp clientIp;

    public FolderController(FolderHandler folderHandler, ClientIp clientIp) {
        this.folderHandler = folderHandler;
        this.clientIp = clientIp;
    }

    public Object create(CreateFolderRequest request, HttpServletRequest httpRequest) {
        return folderHandler.createSafe(request, clientIp.resolve(httpRequest));
    }

    public Object create(CreateFolderRequest request, String clientIpAddress) {
        return folderHandler.createSafe(request, clientIpAddress);
    }

    public Object create(CreateFolderRequest request) {
        return folderHandler.createSafe(request, null);
    }

    public Object get(int folderId) {
        return folderHandler.getSafe(folderId);
    }

    public Object getByUser() {
        return folderHandler.getByUserSafe();
    }

    public Object rename(RenameFolderRequest request, HttpServletRequest httpRequest) {
        return folderHandler.renameSafe(request, clientIp.resolve(httpRequest));
    }

    public Object rename(RenameFolderRequest request, String clientIpAddress) {
        return folderHandler.renameSafe(request, clientIpAddress);
    }

    public Object rename(RenameFolderRequest request) {
        return folderHandler.renameSafe(request, null);
    }

    public Object delete(int folderId, HttpServletRequest httpRequest) {
        return folderHandler.deleteSafe(folderId, clientIp.resolve(httpRequest));
    }

    public Object delete(int folderId, String clientIpAddress) {
        return folderHandler.deleteSafe(folderId, clientIpAddress);
    }

    public Object delete(int folderId) {
        return folderHandler.deleteSafe(folderId, null);
    }
}