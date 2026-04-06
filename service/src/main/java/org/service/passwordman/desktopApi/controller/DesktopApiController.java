package org.service.passwordman.desktopApi.controller;

public class DesktopApiController {

    private final AuthController authController;
    private final VaultController vaultController;
    private final FolderController folderController;
    private final PasswordEntryController passwordEntryController;
    private final AuditController auditController;
    private final PasswordGeneratorController passwordGeneratorController;

    public DesktopApiController(
            AuthController authController,
            VaultController vaultController,
            FolderController folderController,
            PasswordEntryController passwordEntryController,
            AuditController auditController,
            PasswordGeneratorController passwordGeneratorController
    ) {
        this.authController = authController;
        this.vaultController = vaultController;
        this.folderController = folderController;
        this.passwordEntryController = passwordEntryController;
        this.auditController = auditController;
        this.passwordGeneratorController = passwordGeneratorController;
    }

    public AuthController auth() {
        return authController;
    }

    public VaultController vault() {
        return vaultController;
    }

    public FolderController folders() {
        return folderController;
    }

    public PasswordEntryController entries() {
        return passwordEntryController;
    }

    public AuditController audit() {
        return auditController;
    }

    public PasswordGeneratorController generator() {
        return passwordGeneratorController;
    }
}