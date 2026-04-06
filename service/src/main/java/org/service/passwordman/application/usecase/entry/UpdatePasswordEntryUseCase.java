package org.service.passwordman.application.usecase.entry;

public interface UpdatePasswordEntryUseCase {
    void execute(
            int userId,
            int entryId,
            String title,
            String url,
            String username,
            String plainPassword,
            String notes,
            int folderId,
            String jwtTokenId
    );
}