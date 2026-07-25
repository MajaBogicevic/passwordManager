package org.service.passwordman.application.service.entry;

import java.util.List;

import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.usecase.entry.SearchPasswordEntriesUseCase;
import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.exception.VaultLockedException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;

public class SearchPasswordEntriesService implements SearchPasswordEntriesUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final AutoLockUseCase autoLockUseCase;
    private final VaultKeyStore vaultKeyStore;
    private final EncryptionService encryptionService;

    public SearchPasswordEntriesService(
            PasswordEntryRepository passwordEntryRepository,
            AutoLockUseCase autoLockUseCase,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.autoLockUseCase = autoLockUseCase;
        this.vaultKeyStore = vaultKeyStore;
        this.encryptionService = encryptionService;
    }

    @Override
    public List<PasswordEntry> execute(int userId, String titleQuery, String jwtTokenId) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (titleQuery == null || titleQuery.isBlank()) {
            throw new ValidationException("Search query must not be blank.");
        }

        autoLockUseCase.ensureVaultIsActive(userId, jwtTokenId, null);

        byte[] dataEncryptionKey = vaultKeyStore.get(userId, jwtTokenId)
                .orElseThrow(VaultLockedException::new);

        autoLockUseCase.refreshActivity(userId, jwtTokenId);

        List<PasswordEntry> entries = passwordEntryRepository.searchByUserIdAndTitle(userId, titleQuery);
        return PasswordEntryDecryptor.decryptMetadata(entries, dataEncryptionKey, encryptionService);
    }
}