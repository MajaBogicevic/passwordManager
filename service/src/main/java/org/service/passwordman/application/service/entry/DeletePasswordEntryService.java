package org.service.passwordman.application.service.entry;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.DeletePasswordEntryUseCase;
import org.service.passwordman.domain.exception.EntryNotFoundException;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class DeletePasswordEntryService implements DeletePasswordEntryUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final UserRepository userRepository;
    private final VaultSessionStore vaultSessionStore;
    private final AuditLogger auditLogger;

    public DeletePasswordEntryService(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            AuditLogger auditLogger
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.userRepository = userRepository;
        this.vaultSessionStore = vaultSessionStore;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(int userId, int entryId, String ipAddress) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (!vaultSessionStore.isUnlocked(userId)) {
            throw new VaultSessionExpiredException();
        }

        PasswordEntry entry = passwordEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntryNotFoundException(String.valueOf(entryId)));

        if (entry.getUserId() != userId) {
            throw new UnauthorizedVaultAccessException();
        }

        passwordEntryRepository.deleteById(entryId);
        auditLogger.log(userId, "PASSWORD_ENTRY_DELETED", ipAddress);
    }
}