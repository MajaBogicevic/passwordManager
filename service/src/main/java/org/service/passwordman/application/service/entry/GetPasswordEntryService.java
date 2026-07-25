package org.service.passwordman.application.service.entry;

import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.GetPasswordEntryUseCase;
import org.service.passwordman.domain.exception.EntryNotFoundException;
import org.service.passwordman.domain.exception.VaultLockedException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;

public class GetPasswordEntryService implements GetPasswordEntryUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final VaultSessionStore vaultSessionStore;
    private final VaultKeyStore vaultKeyStore;
    private final EncryptionService encryptionService;

    public GetPasswordEntryService(
            PasswordEntryRepository passwordEntryRepository,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.vaultSessionStore = vaultSessionStore;
        this.vaultKeyStore = vaultKeyStore;
        this.encryptionService = encryptionService;
    }

    @Override
    public PasswordEntry execute(int userId, int entryId, String jwtTokenId) {
        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultSessionExpiredException();
        }

        byte[] dataEncryptionKey = vaultKeyStore.get(userId, jwtTokenId)
                .orElseThrow(VaultLockedException::new);

        PasswordEntry entry = passwordEntryRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new EntryNotFoundException(String.valueOf(entryId)));

        return PasswordEntryDecryptor.decryptMetadata(entry, dataEncryptionKey, encryptionService);
    }
}