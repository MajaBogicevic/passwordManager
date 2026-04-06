package org.service.passwordman.application.usecase.auth;

public interface LoginUserUseCase {

    void execute(String username, String loginPassword, String ip);
}