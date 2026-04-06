package org.service.passwordman.application.service.entry;

import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.GetPasswordEntryUseCase;
import org.service.passwordman.domain.exception.EntryNotFoundException;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;

public class GetPasswordEntryService implements GetPasswordEntryUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final VaultSessionStore vaultSessionStore;

    public GetPasswordEntryService(
            PasswordEntryRepository passwordEntryRepository,
            VaultSessionStore vaultSessionStore
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.vaultSessionStore = vaultSessionStore;
    }

    @Override
    public PasswordEntry execute(int userId, int entryId) {
        if (!vaultSessionStore.isUnlocked(userId)) {
            throw new VaultSessionExpiredException();
        }

        if (!vaultSessionStore.isUnlocked(userId)) {
            throw new VaultSessionExpiredException();
        }

        PasswordEntry entry = passwordEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntryNotFoundException(String.valueOf(entryId)));

        if (entry.getUserId() != userId) {
            throw new UnauthorizedVaultAccessException();
        }

        return entry;
    }
}