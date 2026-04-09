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
    public void execute(int userId, int passwordEntryId, String ipAddress, String jwtTokenId) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultSessionExpiredException();
        }

        PasswordEntry entry = passwordEntryRepository.findByIdAndUserId(passwordEntryId, userId)
                .orElseThrow(() -> new EntryNotFoundException(String.valueOf(passwordEntryId)));

        passwordEntryRepository.deleteById(entry.getId());

        auditLogger.log(userId, "PASSWORD_ENTRY_DELETED", ipAddress);
    }
}