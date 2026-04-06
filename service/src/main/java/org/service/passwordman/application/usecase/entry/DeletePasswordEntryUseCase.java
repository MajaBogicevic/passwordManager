package org.service.passwordman.application.usecase.entry;

public interface DeletePasswordEntryUseCase {
    void execute(int userId,int passwordEntryId);
}