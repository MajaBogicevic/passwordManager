package org.service.passwordman.application.service.entry;

import org.service.passwordman.application.usecase.entry.SearchPasswordEntriesUseCase;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;

import java.util.List;

public class SearchPasswordEntriesService implements SearchPasswordEntriesUseCase {

    private final PasswordEntryRepository passwordEntryRepository;

    public SearchPasswordEntriesService(PasswordEntryRepository passwordEntryRepository) {
        this.passwordEntryRepository = passwordEntryRepository;
    }

    @Override
    public List<PasswordEntry> execute(int userId, String titleQuery) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (titleQuery == null) {
            throw new ValidationException("Search query must not be null.");
        }

        return passwordEntryRepository.searchByUserIdAndTitle(userId, titleQuery);
    }
}