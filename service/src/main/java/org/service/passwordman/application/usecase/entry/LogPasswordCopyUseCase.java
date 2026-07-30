package org.service.passwordman.application.usecase.entry;

public interface LogPasswordCopyUseCase {
    void execute(int userId, int entryId, String ipAddress);
}