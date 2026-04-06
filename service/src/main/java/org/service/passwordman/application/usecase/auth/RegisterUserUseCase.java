package org.service.passwordman.application.usecase.auth;

public interface RegisterUserUseCase {

    void execute(String email, String username, String loginPassword, String masterPassword, String notes, String ip);
}