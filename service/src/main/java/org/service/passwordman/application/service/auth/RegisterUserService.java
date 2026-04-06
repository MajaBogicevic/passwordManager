package org.service.passwordman.application.service.auth;

import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.usecase.auth.RegisterUserUseCase;
import org.service.passwordman.domain.exception.UserExistsException;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;

import java.time.LocalDateTime;

public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public RegisterUserService(UserRepository userRepository, PasswordHasher passwordHasher, Clock clock) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    @Override
    public void execute(String email, String username, String loginPassword, String masterPassword, String notes) {
        if (userRepository.existsByUsername(username)) {
            throw new UserExistsException(username);
        }

        LocalDateTime now = clock.now();

        User user = new User(
                0,
                email,
                username,
                passwordHasher.hash(loginPassword),
                passwordHasher.hash(masterPassword),
                notes,
                now,
                now
        );

        userRepository.save(user);
    }
}