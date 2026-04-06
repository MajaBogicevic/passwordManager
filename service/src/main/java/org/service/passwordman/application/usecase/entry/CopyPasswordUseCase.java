package org.service.passwordman.application.usecase.entry;

public interface CopyPasswordUseCase {
    String execute(int userId, int entryId, String ipAddress);
}