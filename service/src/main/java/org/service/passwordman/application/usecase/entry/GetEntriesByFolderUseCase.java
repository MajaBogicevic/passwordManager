package org.service.passwordman.application.usecase.entry;

import org.service.passwordman.domain.model.PasswordEntry;

import java.util.List;

public interface GetEntriesByFolderUseCase {
    List<PasswordEntry> execute(int userId, int folderId);
}