package org.service.passwordman.application.usecase.auth;

import org.service.passwordman.application.security.TokenPayload;

public interface LoginUserUseCase {

    TokenPayload execute(String username, String loginPassword, String ip);
}