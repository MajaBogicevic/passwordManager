package org.service.passwordman.application.usecase.entry;

public interface RevealPasswordUseCase {
    String execute(int userId, int entryId, String ipAddress);
}