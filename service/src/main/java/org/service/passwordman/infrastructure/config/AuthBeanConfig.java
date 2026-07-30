package org.service.passwordman.infrastructure.config;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.RateLimitStore;
import org.service.passwordman.application.port.RefreshTokenStore;
import org.service.passwordman.application.port.TokenBlacklistStore;
import org.service.passwordman.application.port.TokenService;
import org.service.passwordman.application.port.UserAuthInvalidationStore;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.service.auth.ChangePasswordService;
import org.service.passwordman.application.service.auth.LoginUserService;
import org.service.passwordman.application.service.auth.LogoutUserService;
import org.service.passwordman.application.service.auth.RefreshAccessTokenService;
import org.service.passwordman.application.service.auth.RegisterUserService;
import org.service.passwordman.application.usecase.auth.ChangePasswordUseCase;
import org.service.passwordman.application.usecase.auth.LoginUserUseCase;
import org.service.passwordman.application.usecase.auth.LogoutUserUseCase;
import org.service.passwordman.application.usecase.auth.RefreshAccessTokenUseCase;
import org.service.passwordman.application.usecase.auth.RegisterUserUseCase;
import org.service.passwordman.desktopApi.controller.AuthController;
import org.service.passwordman.desktopApi.handler.ApiHandler;
import org.service.passwordman.desktopApi.handler.AuthHandler;
import org.service.passwordman.desktopApi.mapper.AuthDesktopMapper;
import org.service.passwordman.desktopApi.validation.AuthRequestValidator;
import org.service.passwordman.domain.repository.UserRepository;
import org.service.passwordman.infrastructure.security.ClientIp;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthBeanConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            Clock clock,
            AuditLogger auditLogger
    ) {
        return new RegisterUserService(userRepository, passwordHasher, clock, auditLogger);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AuditLogger auditLogger,
            RateLimitStore rateLimitStore,
            PasswordmanProperties properties
    ) {
        return new LoginUserService(
                userRepository,
                passwordHasher,
                auditLogger,
                rateLimitStore,
                properties.getLoginRateLimitMaxAttempts(),
                properties.getLoginRateLimitBlockDurationMillis()
        );
    }

    @Bean
    public LogoutUserUseCase logoutUserUseCase(
            TokenBlacklistStore tokenBlacklistStore,
            RefreshTokenStore refreshTokenStore,
            AuditLogger auditLogger,
            Clock clock,
            UserAuthInvalidationStore userAuthInvalidationStore
    ) {
        return new LogoutUserService(
                tokenBlacklistStore,
                refreshTokenStore,
                auditLogger,
                clock,
                userAuthInvalidationStore
        );
    }

    @Bean
    public RefreshAccessTokenUseCase refreshAccessTokenUseCase(
            TokenService tokenService,
            RefreshTokenStore refreshTokenStore,
            RateLimitStore rateLimitStore,
            PasswordmanProperties properties,
            AuditLogger auditLogger
    ) {
        return new RefreshAccessTokenService(
                tokenService,
                refreshTokenStore,
                auditLogger,
                rateLimitStore,
                properties.getRefreshRateLimitMaxAttempts(),
                properties.getRefreshRateLimitBlockDurationMillis()
        );
    }

    @Bean
    public ChangePasswordUseCase changePasswordUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            AuditLogger auditLogger,
            Clock clock,
            RefreshTokenStore refreshTokenStore,
            UserAuthInvalidationStore userAuthInvalidationStore
    ) {
        return new ChangePasswordService(
                userRepository,
                passwordHasher,
                vaultSessionStore,
                vaultKeyStore,
                auditLogger,
                clock,
                refreshTokenStore,
                userAuthInvalidationStore
        );
    }

    @Bean
    public AuthRequestValidator authRequestValidator() {
        return new AuthRequestValidator();
    }

    @Bean
    public AuthDesktopMapper authDesktopMapper() {
        return new AuthDesktopMapper();
    }

    @Bean
    public AuthHandler authHandler(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            AuthDesktopMapper authDesktopMapper,
            AuthRequestValidator authRequestValidator,
            ApiHandler apiHandler,
            TokenService tokenService,
            CurrentUserProvider currentUserProvider,
            LogoutUserUseCase logoutUserUseCase,
            RefreshTokenStore refreshTokenStore,
            RefreshAccessTokenUseCase refreshAccessTokenUseCase,
            UserRepository userRepository
    ) {
        return new AuthHandler(
                registerUserUseCase,
                loginUserUseCase,
                changePasswordUseCase,
                authDesktopMapper,
                authRequestValidator,
                apiHandler,
                tokenService,
                currentUserProvider,
                logoutUserUseCase,
                refreshTokenStore,
                refreshAccessTokenUseCase,
                userRepository
        );
    }

    @Bean
    public AuthController authController(AuthHandler authHandler, ClientIp clientIp) {
        return new AuthController(authHandler, clientIp);
    }
}