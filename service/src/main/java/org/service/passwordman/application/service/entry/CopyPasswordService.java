package org.service.passwordman.application.service.entry;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.usecase.entry.CopyPasswordUseCase;
import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.domain.exception.EntryNotFoundException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;

public class CopyPasswordService implements CopyPasswordUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final EncryptionService encryptionService;
    private final AutoLockUseCase autoLockUseCase;
    private final AuditLogger auditLogger;

    public CopyPasswordService(
            PasswordEntryRepository passwordEntryRepository,
            EncryptionService encryptionService,
            AutoLockUseCase autoLockUseCase,
            AuditLogger auditLogger
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.encryptionService = encryptionService;
        this.autoLockUseCase = autoLockUseCase;
        this.auditLogger = auditLogger;
    }

    @Override
    public String execute(int userId, int entryId, String ipAddress) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (entryId <= 0) {
            throw new ValidationException("Entry id must be greater than 0.");
        }

        autoLockUseCase.ensureVaultIsActive(userId);

        PasswordEntry entry = passwordEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntryNotFoundException(String.valueOf(entryId)));

        if (entry.getUserId() != userId) {
            throw new EntryNotFoundException(String.valueOf(entryId));
        }

        String plainPassword = encryptionService.decrypt(entry.getEncryptedPassword());

        autoLockUseCase.refreshActivity(userId);
        auditLogger.log(userId, "PASSWORD_COPIED", ipAddress);

        return plainPassword;
    }
}