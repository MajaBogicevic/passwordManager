package org.service.passwordman.application.service.entry;

import java.util.List;

import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.GetEntriesByUserUseCase;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.VaultLockedException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class GetEntriesByUserService implements GetEntriesByUserUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final UserRepository userRepository;
    private final VaultSessionStore vaultSessionStore;
    private final VaultKeyStore vaultKeyStore;
    private final EncryptionService encryptionService;

    public GetEntriesByUserService(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.userRepository = userRepository;
        this.vaultSessionStore = vaultSessionStore;
        this.vaultKeyStore = vaultKeyStore;
        this.encryptionService = encryptionService;
    }

    @Override
    public List<PasswordEntry> execute(int userId, String jwtTokenId) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultSessionExpiredException();
        }

        byte[] dataEncryptionKey = vaultKeyStore.get(userId, jwtTokenId)
                .orElseThrow(VaultLockedException::new);

        List<PasswordEntry> entries = passwordEntryRepository.findByUserId(userId);
        return PasswordEntryDecryptor.decryptMetadata(entries, dataEncryptionKey, encryptionService);
    }
}