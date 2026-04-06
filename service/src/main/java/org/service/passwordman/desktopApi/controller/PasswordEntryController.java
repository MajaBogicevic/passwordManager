package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.PasswordEntryHandler;
import org.service.passwordman.desktopApi.request.CreatePasswordEntryRequest;
import org.service.passwordman.desktopApi.request.SearchPasswordEntriesRequest;
import org.service.passwordman.desktopApi.request.UpdatePasswordEntryRequest;

public class PasswordEntryController {

    private final PasswordEntryHandler passwordEntryHandler;

    public PasswordEntryController(PasswordEntryHandler passwordEntryHandler) {
        this.passwordEntryHandler = passwordEntryHandler;
    }

    public Object create(CreatePasswordEntryRequest request) {
        return passwordEntryHandler.createSafe(request);
    }

    public Object get(int userId, int entryId) {
        return passwordEntryHandler.getSafe(userId, entryId);
    }

    public Object getByUser() {
        return passwordEntryHandler.getByCurrentUserSafe();
    }

    public Object getByFolder(int folderId) {
        return passwordEntryHandler.getByFolderSafe(folderId);
    }

    public Object revealPassword(int entryId, String ipAddress) {
        return passwordEntryHandler.revealPasswordSafe(entryId, ipAddress);
    }

    public Object copyPassword(int entryId, String ipAddress) {
        return passwordEntryHandler.copyPasswordSafe(entryId, ipAddress);
    }

    public Object update(UpdatePasswordEntryRequest request) {
        return passwordEntryHandler.updateSafe(request);
    }

    public Object delete(int entryId, String ipAddress) {
        return passwordEntryHandler.deleteSafe(entryId, ipAddress);
    }

    public Object search(SearchPasswordEntriesRequest request) {
        return passwordEntryHandler.searchSafe(request);
    }
}