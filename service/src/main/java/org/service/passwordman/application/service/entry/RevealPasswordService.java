package org.service.passwordman.application.service.entry;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.usecase.entry.RevealPasswordUseCase;
import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.domain.exception.EntryNotFoundException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.exception.VaultLockedException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;

public class RevealPasswordService implements RevealPasswordUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final VaultKeyStore vaultKeyStore;
    private final EncryptionService encryptionService;
    private final AutoLockUseCase autoLockUseCase;
    private final AuditLogger auditLogger;

    public RevealPasswordService(
            PasswordEntryRepository passwordEntryRepository,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService,
            AutoLockUseCase autoLockUseCase,
            AuditLogger auditLogger
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.vaultKeyStore = vaultKeyStore;
        this.encryptionService = encryptionService;
        this.autoLockUseCase = autoLockUseCase;
        this.auditLogger = auditLogger;
    }

    @Override
    public String execute(int userId, int entryId, String ipAddress, String jwtTokenId) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (entryId <= 0) {
            throw new ValidationException("Entry id must be greater than 0.");
        }

        autoLockUseCase.ensureVaultIsActive(userId, jwtTokenId, ipAddress);

        byte[] dataEncryptionKey = vaultKeyStore.get(userId, jwtTokenId)
                .orElseThrow(VaultLockedException::new);

        PasswordEntry entry = passwordEntryRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new EntryNotFoundException(String.valueOf(entryId)));

        String plainPassword = encryptionService.decrypt(dataEncryptionKey, entry.getEncryptedPassword());

        autoLockUseCase.refreshActivity(userId, jwtTokenId);
        auditLogger.log(userId, "PASSWORD_REVEAL_SUCCESS", ipAddress);

        return plainPassword;
    }
}