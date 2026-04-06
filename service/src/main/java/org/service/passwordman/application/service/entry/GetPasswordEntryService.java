package org.service.passwordman.application.service.entry;

import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.GetPasswordEntryUseCase;
import org.service.passwordman.domain.exception.EntryNotFoundException;
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
    public PasswordEntry execute(int userId, int entryId, String jwtTokenId) {
        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultSessionExpiredException();
        }

        return passwordEntryRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new EntryNotFoundException(String.valueOf(entryId)));
    }
}