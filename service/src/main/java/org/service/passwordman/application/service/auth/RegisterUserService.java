package org.service.passwordman.application.service.auth;

import java.time.LocalDateTime;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.security.SecurityAuditEvent;
import org.service.passwordman.application.usecase.auth.RegisterUserUseCase;
import org.service.passwordman.domain.exception.UserExistsException;
import org.service.passwordman.domain.model.SecurityEventType;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;

public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;
    private final AuditLogger auditLogger;

    public RegisterUserService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            Clock clock,
            AuditLogger auditLogger
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(String email, String username, String loginPassword, String masterPassword, String notes, String ip) {
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

        User persistedUser = userRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("User was saved but could not be loaded back for audit logging."));

        auditLogger.log(SecurityAuditEvent.success(
                persistedUser.getId(),
                SecurityEventType.USER_REGISTERED,
                ip,
                null,
                "User registration succeeded."
        ));
    }
}