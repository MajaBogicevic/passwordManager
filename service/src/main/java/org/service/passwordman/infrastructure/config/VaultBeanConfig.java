package org.service.passwordman.infrastructure.config;

import java.time.Duration;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.RateLimitStore;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.service.vault.AutoLockService;
import org.service.passwordman.application.service.vault.LockVaultService;
import org.service.passwordman.application.service.vault.UnlockVaultService;
import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.application.usecase.vault.LockVaultUseCase;
import org.service.passwordman.application.usecase.vault.UnlockVaultUseCase;
import org.service.passwordman.desktopApi.controller.VaultController;
import org.service.passwordman.desktopApi.handler.ApiHandler;
import org.service.passwordman.desktopApi.handler.VaultHandler;
import org.service.passwordman.desktopApi.mapper.AuthDesktopMapper;
import org.service.passwordman.desktopApi.validation.AuthRequestValidator;
import org.service.passwordman.domain.repository.UserRepository;
import org.service.passwordman.infrastructure.security.ClientIp;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VaultBeanConfig {

    @Bean
    public UnlockVaultUseCase unlockVaultUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            AuditLogger auditLogger,
            Clock clock,
            RateLimitStore rateLimitStore,
            PasswordmanProperties properties
    ) {
        return new UnlockVaultService(
                userRepository,
                passwordHasher,
                vaultSessionStore,
                vaultKeyStore,
                auditLogger,
                clock,
                rateLimitStore,
                properties.getVaultUnlockRateLimitMaxAttempts(),
                properties.getVaultUnlockRateLimitBlockDurationMillis()
        );
    }

    @Bean
    public LockVaultUseCase lockVaultUseCase(
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            AuditLogger auditLogger
    ) {
        return new LockVaultService(vaultSessionStore, vaultKeyStore, auditLogger);
    }

    @Bean
    public AutoLockUseCase autoLockUseCase(
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            AuditLogger auditLogger,
            Clock clock
    ) {
        return new AutoLockService(
                vaultSessionStore,
                vaultKeyStore,
                clock,
                auditLogger,
                Duration.ofMinutes(5)
        );
    }

    @Bean
    public VaultHandler vaultHandler(
            UnlockVaultUseCase unlockVaultUseCase,
            LockVaultUseCase lockVaultUseCase,
            AutoLockUseCase autoLockUseCase,
            AuthDesktopMapper authDesktopMapper,
            AuthRequestValidator authRequestValidator,
            ApiHandler apiHandler,
            CurrentUserProvider currentUserProvider
    ) {
        return new VaultHandler(
                unlockVaultUseCase,
                lockVaultUseCase,
                autoLockUseCase,
                authDesktopMapper,
                authRequestValidator,
                apiHandler,
                currentUserProvider
        );
    }

    @Bean
    public VaultController vaultController(VaultHandler vaultHandler, ClientIp clientIp) {
        return new VaultController(vaultHandler, clientIp);
    }
}