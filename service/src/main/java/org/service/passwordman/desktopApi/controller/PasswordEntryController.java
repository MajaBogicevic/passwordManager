package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.PasswordEntryHandler;
import org.service.passwordman.desktopApi.request.CreatePasswordEntryRequest;
import org.service.passwordman.desktopApi.request.UpdatePasswordEntryRequest;
import org.service.passwordman.desktopApi.request.SearchPasswordEntriesRequest;

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

    public Object getByUser(int userId) {
        return passwordEntryHandler.getByUserSafe(userId);
    }

    public Object getByFolder(int userId, int folderId) {
        return passwordEntryHandler.getByFolderSafe(userId, folderId);
    }

    public Object revealPassword(int userId, int entryId) {
        return passwordEntryHandler.revealPasswordSafe(userId, entryId);
    }

    public Object copyPassword(int userId, int entryId) {
        return passwordEntryHandler.copyPasswordSafe(userId, entryId);
    }

    public Object update(UpdatePasswordEntryRequest request) {
        return passwordEntryHandler.updateSafe(request);
    }

    public Object delete(int userId, int entryId) {
        return passwordEntryHandler.deleteSafe(userId, entryId);
    }

    public Object search(SearchPasswordEntriesRequest request) {
        return passwordEntryHandler.searchSafe(request);
    }
}