package org.service.passwordman.application.usecase.auth;

public interface ChangeLoginPasswordUseCase {
    void execute(int userId, String oldLoginPassword, String newLoginPassword);
}