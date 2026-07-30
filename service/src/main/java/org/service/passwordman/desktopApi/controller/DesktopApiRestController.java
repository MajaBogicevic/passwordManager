package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.request.ChangePasswordRequest;
import org.service.passwordman.desktopApi.request.CreateFolderRequest;
import org.service.passwordman.desktopApi.request.CreatePasswordEntryRequest;
import org.service.passwordman.desktopApi.request.LoginRequest;
import org.service.passwordman.desktopApi.request.LogoutRequest;
import org.service.passwordman.desktopApi.request.RefreshTokenRequest;
import org.service.passwordman.desktopApi.request.RegisterRequest;
import org.service.passwordman.desktopApi.request.RenameFolderRequest;
import org.service.passwordman.desktopApi.request.SearchPasswordEntriesRequest;
import org.service.passwordman.desktopApi.request.UnlockVaultRequest;
import org.service.passwordman.desktopApi.request.UpdatePasswordEntryRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
public class DesktopApiRestController {

    private final DesktopApiController desktopApiController;

    public DesktopApiRestController(DesktopApiController desktopApiController) {
        this.desktopApiController = desktopApiController;
    }

    @PostMapping("/auth/register")
    public Object register(
            @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.auth().register(request, httpRequest);
    }

    @PostMapping("/auth/login")
    public Object login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.auth().login(request, httpRequest);
    }

    @PostMapping("/auth/change-password")
    public Object changePassword(
            @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.auth().changePassword(request, httpRequest);
    }

    @GetMapping("/users/me")
    public Object getCurrentUser() {
        return desktopApiController.auth().me();
    }

    @PostMapping("/auth/logout")
    public Object logout(
            @RequestBody LogoutRequest request,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.auth().logout(request, httpRequest);
    }

    @PostMapping("/auth/refresh")
    public Object refreshToken(
            @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.auth().refreshToken(request, httpRequest);
    }

    @PostMapping("/vault/unlock")
    public Object unlockVault(
            @RequestBody UnlockVaultRequest request,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.vault().unlock(request, httpRequest);
    }

    @PostMapping("/vault/lock")
    public Object lockVault(HttpServletRequest httpRequest) {
        return desktopApiController.vault().lock(httpRequest);
    }

    @PostMapping("/vault/auto-lock")
    public Object autoLockVault(HttpServletRequest httpRequest) {
        return desktopApiController.vault().autoLock(httpRequest);
    }

    @PostMapping("/folders")
    public Object createFolder(
            @RequestBody CreateFolderRequest request,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.folders().create(request, httpRequest);
    }

    @GetMapping("/folders/{folderId}")
    public Object getFolder(@PathVariable int folderId) {
        return desktopApiController.folders().get(folderId);
    }

    @GetMapping("/folders")
    public Object getFoldersByUser() {
        return desktopApiController.folders().getByUser();
    }

    @PutMapping("/folders")
    public Object renameFolder(
            @RequestBody RenameFolderRequest request,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.folders().rename(request, httpRequest);
    }

    @DeleteMapping("/folders/{folderId}")
    public Object deleteFolder(
            @PathVariable int folderId,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.folders().delete(folderId, httpRequest);
    }

    @PostMapping("/entries")
    public Object createEntry(
            @RequestBody CreatePasswordEntryRequest request,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.entries().create(request, httpRequest);
    }

    @GetMapping("/entries/{entryId}")
    public Object getEntry(@PathVariable int entryId) {
        return desktopApiController.entries().get(entryId);
    }

    @GetMapping("/entries")
    public Object getEntriesByUser() {
        return desktopApiController.entries().getByUser();
    }

    @GetMapping("/folders/{folderId}/entries")
    public Object getEntriesByFolder(@PathVariable int folderId) {
        return desktopApiController.entries().getByFolder(folderId);
    }

    @PostMapping("/entries/{entryId}/reveal")
    public Object revealPassword(
            @PathVariable int entryId,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.entries().revealPassword(entryId, httpRequest);
    }

    @PostMapping("/entries/{entryId}/log-copy")
    public Object logPasswordCopy(
            @PathVariable int entryId,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.entries().logPasswordCopy(entryId, httpRequest);
    }

    @PutMapping("/entries")
    public Object updateEntry(
            @RequestBody UpdatePasswordEntryRequest request,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.entries().update(request, httpRequest);
    }

    @DeleteMapping("/entries/{entryId}")
    public Object deleteEntry(
            @PathVariable int entryId,
            HttpServletRequest httpRequest
    ) {
        return desktopApiController.entries().delete(entryId, httpRequest);
    }

    @PostMapping("/entries/search")
    public Object searchEntries(@RequestBody SearchPasswordEntriesRequest request) {
        return desktopApiController.entries().search(request);
    }

    @GetMapping("/audit/activity")
    public Object getSecurityActivity(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String outcome,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return desktopApiController.audit().getSecurityActivity(eventType, outcome, fromDate, toDate, page, size);
    }

    @GetMapping("/generator/password")
    public Object generatePassword(
            @RequestParam int length,
            @RequestParam boolean useUppercase,
            @RequestParam boolean useLowercase,
            @RequestParam boolean useDigits,
            @RequestParam boolean useSymbols
    ) {
        return desktopApiController.generator().generate(
                length,
                useUppercase,
                useLowercase,
                useDigits,
                useSymbols
        );
    }
}