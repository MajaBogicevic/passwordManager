package org.service.passwordman.application.service.auth;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.usecase.auth.ChangeLoginPasswordUseCase;
import org.service.passwordman.domain.exception.ValidationException;
import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;
import org.service.passwordman.domain.exception.UserNotFoundException;
import org.service.passwordman.domain.exception.InvalidCredentialsException;

public class ChangeLoginPasswordService implements ChangeLoginPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AuditLogger auditLogger;
    private final Clock clock;

    public ChangeLoginPasswordService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AuditLogger auditLogger,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.auditLogger = auditLogger;
        this.clock = clock;
    }

    @Override
    public void execute(int userId, String oldLoginPassword, String newLoginPassword, String ipAddress) {
        if (userId <= 0) {
            throw new ValidationException("User id must be greater than 0.");
        }

        if (oldLoginPassword == null || oldLoginPassword.trim().isEmpty()) {
            throw new ValidationException("Old login password is required.");
        }

        if (newLoginPassword == null || newLoginPassword.trim().isEmpty()) {
            throw new ValidationException("New login password is required.");
        }

        if (oldLoginPassword.equals(newLoginPassword)) {
            throw new ValidationException("New login password must be different from the old login password.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordHasher.matches(oldLoginPassword, user.getLoginPasswordHash())) {
            auditLogger.log(userId, "CHANGE_LOGIN_PASSWORD_FAILED", ipAddress);
            throw new InvalidCredentialsException();
        }

        String newHash = passwordHasher.hash(newLoginPassword);

        User updatedUser = new User(
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            newHash, 
            user.getMasterPasswordHash(), 
            user.getNotes(),
            user.getCreatedAt(),
            clock.now()
        );

        userRepository.save(updatedUser);

        auditLogger.log(userId, "CHANGE_LOGIN_PASSWORD_SUCCESS", ipAddress);
    }
}
