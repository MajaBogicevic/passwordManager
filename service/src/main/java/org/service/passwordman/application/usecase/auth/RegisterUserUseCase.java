package org.service.passwordman.application.usecase.auth;

public interface RegisterUserUseCase {

    void execute(String email, String username, String password, String notes, String ip);
}