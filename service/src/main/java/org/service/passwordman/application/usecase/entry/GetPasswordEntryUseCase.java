package org.service.passwordman.application.usecase.entry;

import org.service.passwordman.domain.model.PasswordEntry;

public interface GetPasswordEntryUseCase {
    PasswordEntry execute(int userId, int entryId);
}