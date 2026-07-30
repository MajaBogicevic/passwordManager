package org.service.passwordman.application.service.entry;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.usecase.entry.LogPasswordCopyUseCase;
import org.service.passwordman.domain.exception.EntryNotFoundException;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.SecurityEventType;
import org.service.passwordman.domain.repository.PasswordEntryRepository;

public class LogPasswordCopyService implements LogPasswordCopyUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final AuditLogger auditLogger;

    public LogPasswordCopyService(
            PasswordEntryRepository passwordEntryRepository,
            AuditLogger auditLogger
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(int userId, int entryId, String ipAddress) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (entryId <= 0) {
            throw new ValidationException("Entry id must be greater than 0.");
        }

        passwordEntryRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new EntryNotFoundException(String.valueOf(entryId)));

        auditLogger.log(SecurityAuditEvent.success(
                userId,
                SecurityEventType.PASSWORD_COPIED,
                ipAddress,
                null,
                "Password copied to clipboard for entry " + entryId + "."
        ));
    }
}