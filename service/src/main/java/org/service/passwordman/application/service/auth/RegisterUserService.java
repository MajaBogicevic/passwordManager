package org.service.passwordman.application.service.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.crypt.crypto.kdf.DataKeyWrapper;
import org.crypt.crypto.kdf.Pbkdf2KeyDerivation;
import org.crypt.crypto.util.Base64Url;
import org.crypt.crypto.util.ZeroUtils;
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

    private static final int DEK_LENGTH_BYTES = 32;

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;
    private final AuditLogger auditLogger;
    private final SecureRandom secureRandom = new SecureRandom();

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
    public void execute(String email, String username, String password, String notes, String ip) {
        if (userRepository.existsByUsername(username)) {
            throw new UserExistsException(username);
        }

        LocalDateTime now = clock.now();

        byte[] dataEncryptionKey = new byte[DEK_LENGTH_BYTES];
        secureRandom.nextBytes(dataEncryptionKey);

        byte[] salt = Pbkdf2KeyDerivation.generateSalt();
        char[] passwordChars = password.toCharArray();
        byte[] keyEncryptionKey = null;

        String wrappedDataKey;
        try {
            keyEncryptionKey = Pbkdf2KeyDerivation.deriveKey(passwordChars, salt);
            wrappedDataKey = DataKeyWrapper.wrap(dataEncryptionKey, keyEncryptionKey);
        } finally {
            java.util.Arrays.fill(passwordChars, '\0');
            ZeroUtils.zero(keyEncryptionKey);
            ZeroUtils.zero(dataEncryptionKey);
        }

        User user = new User(
                0,
                email,
                username,
                passwordHasher.hash(password),
                Base64Url.encode(salt),
                wrappedDataKey,
                notes,
                now,
                now
        );

        userRepository.save(user);

        User persistedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User was saved but could not be loaded back for audit logging."));

        auditLogger.log(SecurityAuditEvent.success(
                persistedUser.getId(),
                SecurityEventType.USER_REGISTERED,
                ip,
                null,
                "User registration succeeded."
        ));
    }
}