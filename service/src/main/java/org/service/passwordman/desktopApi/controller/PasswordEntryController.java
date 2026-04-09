package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.PasswordEntryHandler;
import org.service.passwordman.desktopApi.request.CreatePasswordEntryRequest;
import org.service.passwordman.desktopApi.request.SearchPasswordEntriesRequest;
import org.service.passwordman.desktopApi.request.UpdatePasswordEntryRequest;
import org.service.passwordman.infrastructure.security.ClientIp;

import jakarta.servlet.http.HttpServletRequest;

public class PasswordEntryController {

    private final PasswordEntryHandler passwordEntryHandler;
    private final ClientIp clientIp;

    public PasswordEntryController(PasswordEntryHandler passwordEntryHandler, ClientIp clientIp) {
        this.passwordEntryHandler = passwordEntryHandler;
        this.clientIp = clientIp;
    }

    public Object create(CreatePasswordEntryRequest request, HttpServletRequest httpRequest) {
        return passwordEntryHandler.createSafe(request, clientIp.resolve(httpRequest));
    }

    public Object create(CreatePasswordEntryRequest request, String clientIpAddress) {
        return passwordEntryHandler.createSafe(request, clientIpAddress);
    }

    public Object get(int entryId) {
        return passwordEntryHandler.getSafe(entryId);
    }

    public Object getByUser() {
        return passwordEntryHandler.getByCurrentUserSafe();
    }

    public Object getByFolder(int folderId) {
        return passwordEntryHandler.getByFolderSafe(folderId);
    }

    public Object revealPassword(int entryId, HttpServletRequest httpRequest) {
        return passwordEntryHandler.revealPasswordSafe(entryId, clientIp.resolve(httpRequest));
    }

    public Object revealPassword(int entryId, String clientIpAddress) {
        return passwordEntryHandler.revealPasswordSafe(entryId, clientIpAddress);
    }

    public Object update(UpdatePasswordEntryRequest request, HttpServletRequest httpRequest) {
        return passwordEntryHandler.updateSafe(request, clientIp.resolve(httpRequest));
    }

    public Object update(UpdatePasswordEntryRequest request, String clientIpAddress) {
        return passwordEntryHandler.updateSafe(request, clientIpAddress);
    }

    public Object update(UpdatePasswordEntryRequest request) {
        return passwordEntryHandler.updateSafe(request, null);
    }

    public Object delete(int entryId, HttpServletRequest httpRequest) {
        return passwordEntryHandler.deleteSafe(entryId, clientIp.resolve(httpRequest));
    }

    public Object delete(int entryId, String clientIpAddress) {
        return passwordEntryHandler.deleteSafe(entryId, clientIpAddress);
    }

    public Object search(SearchPasswordEntriesRequest request) {
        return passwordEntryHandler.searchSafe(request);
    }
}