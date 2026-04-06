package org.service.passwordman.application.usecase.entry;

public interface CreatePasswordEntryUseCase {
    void execute(int userId, String title, String url, String username, String plaintextPassword, String notes, int folderId);
}