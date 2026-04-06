package org.service.passwordman.application.service.entry;

import java.util.List;

import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.usecase.entry.GetEntriesByUserUseCase;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.exception.VaultSessionExpiredException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;

public class GetEntriesByUserService implements GetEntriesByUserUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final UserRepository userRepository;
    private final VaultSessionStore vaultSessionStore;

    public GetEntriesByUserService(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.userRepository = userRepository;
        this.vaultSessionStore = vaultSessionStore;
    }

    @Override
    public List<PasswordEntry> execute(int userId, String jwtTokenId) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        if (!vaultSessionStore.isUnlocked(userId, jwtTokenId)) {
            throw new VaultSessionExpiredException();
        }

        return passwordEntryRepository.findByUserId(userId);
    }
}