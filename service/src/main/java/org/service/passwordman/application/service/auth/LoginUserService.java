package org.service.passwordman.application.service.auth;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.security.TokenPayload;
import org.service.passwordman.application.usecase.auth.LoginUserUseCase;
import org.service.passwordman.domain.exception.InvalidCredentialsException;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;

import java.util.UUID;

public class LoginUserService implements LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AuditLogger auditLogger;

    public LoginUserService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AuditLogger auditLogger
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.auditLogger = auditLogger;
    }

    @Override
    public TokenPayload execute(String username, String loginPassword, String ip) {

        User user = userRepository.findByUsername(username).orElse(null);
        String jwtTokenId = UUID.randomUUID().toString();

        if (user == null) {
            auditLogger.log(0, "LOGIN_FAILED", ip);
            throw new InvalidCredentialsException();
        }

        boolean matches = passwordHasher.matches(loginPassword, user.getLoginPasswordHash());

        if (!matches) {
            auditLogger.log(user.getId(), "LOGIN_FAILED", ip);
            throw new InvalidCredentialsException();
        }

        auditLogger.log(user.getId(), "LOGIN_SUCCESS", ip);

        return new TokenPayload(
                user.getId(),
                user.getUsername(),
                jwtTokenId
        );
    }
}