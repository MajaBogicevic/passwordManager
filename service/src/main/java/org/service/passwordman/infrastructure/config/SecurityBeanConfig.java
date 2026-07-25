package org.service.passwordman.infrastructure.config;
import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.RateLimitStore;
import org.service.passwordman.application.port.TokenBlacklistStore;
import org.service.passwordman.application.port.TokenService;
import org.service.passwordman.application.port.UserAuthInvalidationStore;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.domain.repository.AuditLogRepository;
import org.service.passwordman.infrastructure.audit.AuditLoggerAdapter;
import org.service.passwordman.infrastructure.crypt.CryptPasswordEncryptionAdapter;
import org.service.passwordman.infrastructure.security.BCryptPasswordHasher;
import org.service.passwordman.infrastructure.security.ClientIp;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;
import org.service.passwordman.infrastructure.security.InMemoryRateLimitStore;
import org.service.passwordman.infrastructure.security.InMemoryTokenBlacklistStore;
import org.service.passwordman.infrastructure.security.InMemoryUserAuthInvalidationStore;
import org.service.passwordman.infrastructure.security.JwtTokenService;
import org.service.passwordman.infrastructure.session.InMemoryVaultKeyStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PasswordmanProperties.class)
public class SecurityBeanConfig {

    @Bean
    public TokenService tokenService(PasswordmanProperties properties) {
        properties.validate();

        return new JwtTokenService(
                properties.getJwtSecret(),
                properties.getJwtAccessExpirationMillis(),
                properties.getJwtRefreshExpirationMillis()
        );
    }

    @Bean
    public TokenBlacklistStore tokenBlacklistStore() {
        return new InMemoryTokenBlacklistStore();
    }

    @Bean
    public UserAuthInvalidationStore userAuthInvalidationStore() {
        return new InMemoryUserAuthInvalidationStore();
    }

    @Bean
    public Clock clock() {
        return new SystemClockAdapter();
    }

    @Bean
    public PasswordHasher passwordHasher(PasswordmanProperties properties) {
        return new BCryptPasswordHasher(properties.getBcryptStrength());
    }

    @Bean
    public EncryptionService encryptionService() {
        return new CryptPasswordEncryptionAdapter();
    }

    @Bean
    public VaultKeyStore vaultKeyStore() {
        return new InMemoryVaultKeyStore();
    }

    @Bean
    public RateLimitStore rateLimitStore() {
        return new InMemoryRateLimitStore();
    }

    @Bean
    public AuditLogger auditLogger(AuditLogRepository auditLogRepository, Clock clock) {
        return new AuditLoggerAdapter(auditLogRepository, clock);
    }

    @Bean
    public CurrentUserProvider currentUserProvider() {
        return new CurrentUserProvider();
    }

    @Bean
    public ClientIp clientIp() {
        return new ClientIp();
    }
}