package org.service.passwordman.application.service.entry;

import java.util.List;

import org.service.passwordman.application.usecase.entry.SearchPasswordEntriesUseCase;
import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;

public class SearchPasswordEntriesService implements SearchPasswordEntriesUseCase {

    private final PasswordEntryRepository passwordEntryRepository;
    private final AutoLockUseCase autoLockUseCase;

    public SearchPasswordEntriesService(
            PasswordEntryRepository passwordEntryRepository,
            AutoLockUseCase autoLockUseCase
    ) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.autoLockUseCase = autoLockUseCase;
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
        autoLockUseCase.refreshActivity(userId, jwtTokenId);

        return passwordEntryRepository.searchByUserIdAndTitle(userId, titleQuery);
    }
}