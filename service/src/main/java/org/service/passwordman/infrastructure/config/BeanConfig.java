package org.service.passwordman.infrastructure.config;

import java.time.Duration;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.RateLimitStore;
import org.service.passwordman.application.port.RefreshTokenStore;
import org.service.passwordman.application.port.TokenBlacklistStore;
import org.service.passwordman.application.port.TokenService;
import org.service.passwordman.application.port.UserAuthInvalidationStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.service.audit.GetSecurityActivityService;
import org.service.passwordman.application.service.auth.ChangeLoginPasswordService;
import org.service.passwordman.application.service.auth.ChangeMasterPasswordService;
import org.service.passwordman.application.service.auth.LoginUserService;
import org.service.passwordman.application.service.auth.LogoutUserService;
import org.service.passwordman.application.service.auth.RefreshAccessTokenService;
import org.service.passwordman.application.service.auth.RegisterUserService;
import org.service.passwordman.application.service.entry.CreatePasswordEntryService;
import org.service.passwordman.application.service.entry.DeletePasswordEntryService;
import org.service.passwordman.application.service.entry.GetEntriesByFolderService;
import org.service.passwordman.application.service.entry.GetEntriesByUserService;
import org.service.passwordman.application.service.entry.GetPasswordEntryService;
import org.service.passwordman.application.service.entry.RevealPasswordService;
import org.service.passwordman.application.service.entry.SearchPasswordEntriesService;
import org.service.passwordman.application.service.entry.UpdatePasswordEntryService;
import org.service.passwordman.application.service.folder.CreateFolderService;
import org.service.passwordman.application.service.folder.DeleteFolderService;
import org.service.passwordman.application.service.folder.GetFolderService;
import org.service.passwordman.application.service.folder.GetFoldersByUserService;
import org.service.passwordman.application.service.folder.RenameFolderService;
import org.service.passwordman.application.service.generator.GeneratePasswordService;
import org.service.passwordman.application.service.vault.AutoLockService;
import org.service.passwordman.application.service.vault.LockVaultService;
import org.service.passwordman.application.service.vault.UnlockVaultService;
import org.service.passwordman.application.usecase.audit.GetSecurityActivityUseCase;
import org.service.passwordman.application.usecase.auth.ChangeLoginPasswordUseCase;
import org.service.passwordman.application.usecase.auth.ChangeMasterPasswordUseCase;
import org.service.passwordman.application.usecase.auth.LoginUserUseCase;
import org.service.passwordman.application.usecase.auth.LogoutUserUseCase;
import org.service.passwordman.application.usecase.auth.RefreshAccessTokenUseCase;
import org.service.passwordman.application.usecase.auth.RegisterUserUseCase;
import org.service.passwordman.application.usecase.entry.CreatePasswordEntryUseCase;
import org.service.passwordman.application.usecase.entry.DeletePasswordEntryUseCase;
import org.service.passwordman.application.usecase.entry.GetEntriesByFolderUseCase;
import org.service.passwordman.application.usecase.entry.GetEntriesByUserUseCase;
import org.service.passwordman.application.usecase.entry.GetPasswordEntryUseCase;
import org.service.passwordman.application.usecase.entry.RevealPasswordUseCase;
import org.service.passwordman.application.usecase.entry.SearchPasswordEntriesUseCase;
import org.service.passwordman.application.usecase.entry.UpdatePasswordEntryUseCase;
import org.service.passwordman.application.usecase.folder.CreateFolderUseCase;
import org.service.passwordman.application.usecase.folder.DeleteFolderUseCase;
import org.service.passwordman.application.usecase.folder.GetFolderUseCase;
import org.service.passwordman.application.usecase.folder.GetFoldersByUserUseCase;
import org.service.passwordman.application.usecase.folder.RenameFolderUseCase;
import org.service.passwordman.application.usecase.generator.GeneratePasswordUseCase;
import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.application.usecase.vault.LockVaultUseCase;
import org.service.passwordman.application.usecase.vault.UnlockVaultUseCase;
import org.service.passwordman.desktopApi.controller.AuditController;
import org.service.passwordman.desktopApi.controller.AuthController;
import org.service.passwordman.desktopApi.controller.DesktopApiController;
import org.service.passwordman.desktopApi.controller.FolderController;
import org.service.passwordman.desktopApi.controller.PasswordEntryController;
import org.service.passwordman.desktopApi.controller.PasswordGeneratorController;
import org.service.passwordman.desktopApi.controller.VaultController;
import org.service.passwordman.desktopApi.handler.ApiHandler;
import org.service.passwordman.desktopApi.handler.AuditHandler;
import org.service.passwordman.desktopApi.handler.AuthHandler;
import org.service.passwordman.desktopApi.handler.FolderHandler;
import org.service.passwordman.desktopApi.handler.PasswordEntryHandler;
import org.service.passwordman.desktopApi.handler.PasswordGeneratorHandler;
import org.service.passwordman.desktopApi.handler.VaultHandler;
import org.service.passwordman.desktopApi.mapper.AuthDesktopMapper;
import org.service.passwordman.desktopApi.mapper.ErrorDesktopMapper;
import org.service.passwordman.desktopApi.mapper.FolderDesktopMapper;
import org.service.passwordman.desktopApi.mapper.PasswordEntryDesktopMapper;
import org.service.passwordman.desktopApi.validation.AuthRequestValidator;
import org.service.passwordman.desktopApi.validation.FolderRequestValidator;
import org.service.passwordman.desktopApi.validation.PasswordEntryRequestValidator;
import org.service.passwordman.domain.repository.AuditLogRepository;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;
import org.service.passwordman.infrastructure.audit.AuditLoggerAdapter;
import org.service.passwordman.infrastructure.crypt.CryptPasswordEncryptionAdapter;
import org.service.passwordman.infrastructure.persistence.adapter.JpaAuditLogRepositoryAdapter;
import org.service.passwordman.infrastructure.persistence.adapter.JpaFolderRepositoryAdapter;
import org.service.passwordman.infrastructure.persistence.adapter.JpaPasswordEntryRepositoryAdapter;
import org.service.passwordman.infrastructure.persistence.adapter.JpaRefreshTokenStoreAdapter;
import org.service.passwordman.infrastructure.persistence.adapter.JpaUserRepositoryAdapter;
import org.service.passwordman.infrastructure.persistence.adapter.JpaVaultSessionStoreAdapter;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataAuditLogJpaRepository;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataFolderJpaRepository;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataPasswordEntryJpaRepository;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataRefreshTokenJpaRepository;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataUserJpaRepository;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataVaultSessionJpaRepository;
import org.service.passwordman.infrastructure.security.BCryptPasswordHasher;
import org.service.passwordman.infrastructure.security.ClientIp;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;
import org.service.passwordman.infrastructure.security.InMemoryRateLimitStore;
import org.service.passwordman.infrastructure.security.InMemoryTokenBlacklistStore;
import org.service.passwordman.infrastructure.security.InMemoryUserAuthInvalidationStore;
import org.service.passwordman.infrastructure.security.JwtTokenService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PasswordmanProperties.class)
public class BeanConfig {

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
    public RefreshTokenStore refreshTokenStore(SpringDataRefreshTokenJpaRepository repository) {
        return new JpaRefreshTokenStoreAdapter(repository);
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
    public EncryptionService encryptionService(PasswordmanProperties properties) {
        return new CryptPasswordEncryptionAdapter(
                properties.getEncryptionKeyRef(),
                properties.getEncryptionMasterKeyBase64()
        );
    }

    @Bean
    public UserRepository userRepository(SpringDataUserJpaRepository repository) {
        return new JpaUserRepositoryAdapter(repository);
    }

    @Bean
    public PasswordEntryRepository passwordEntryRepository(SpringDataPasswordEntryJpaRepository repository) {
        return new JpaPasswordEntryRepositoryAdapter(repository);
    }

    @Bean
    public FolderRepository folderRepository(SpringDataFolderJpaRepository repository) {
        return new JpaFolderRepositoryAdapter(repository);
    }

    @Bean
    public AuditLogRepository auditLogRepository(SpringDataAuditLogJpaRepository repository) {
        return new JpaAuditLogRepositoryAdapter(repository);
    }

    @Bean
    public VaultSessionStore vaultSessionStore(SpringDataVaultSessionJpaRepository repository) {
        return new JpaVaultSessionStoreAdapter(repository);
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
    public UnlockVaultUseCase unlockVaultUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            VaultSessionStore vaultSessionStore,
            AuditLogger auditLogger,
            Clock clock,
            RateLimitStore rateLimitStore,
            PasswordmanProperties properties
    ) {
        return new UnlockVaultService(
                userRepository,
                passwordHasher,
                vaultSessionStore,
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
            AuditLogger auditLogger
    ) {
        return new LockVaultService(vaultSessionStore, auditLogger);
    }

    @Bean
    public AutoLockUseCase autoLockUseCase(
            VaultSessionStore vaultSessionStore,
            AuditLogger auditLogger,
            Clock clock
    ) {
        return new AutoLockService(
                vaultSessionStore,
                clock,
                auditLogger,
                Duration.ofMinutes(5)
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
    public ChangeLoginPasswordUseCase changeLoginPasswordUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AuditLogger auditLogger,
            Clock clock,
            RefreshTokenStore refreshTokenStore,
            VaultSessionStore vaultSessionStore,
            UserAuthInvalidationStore userAuthInvalidationStore
    ) {
        return new ChangeLoginPasswordService(
                userRepository,
                passwordHasher,
                auditLogger,
                clock,
                refreshTokenStore,
                vaultSessionStore,
                userAuthInvalidationStore
        );
    }

    @Bean
    public ChangeMasterPasswordUseCase changeMasterPasswordUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            VaultSessionStore vaultSessionStore,
            AuditLogger auditLogger,
            Clock clock,
            RefreshTokenStore refreshTokenStore,
            UserAuthInvalidationStore userAuthInvalidationStore
    ) {
        return new ChangeMasterPasswordService(
                userRepository,
                passwordHasher,
                vaultSessionStore,
                auditLogger,
                clock,
                refreshTokenStore,
                userAuthInvalidationStore
        );
    }

    @Bean
    public CreatePasswordEntryUseCase createPasswordEntryUseCase(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            EncryptionService encryptionService,
            Clock clock,
            AuditLogger auditLogger,
            FolderRepository folderRepository
    ) {
        return new CreatePasswordEntryService(
                passwordEntryRepository,
                userRepository,
                vaultSessionStore,
                encryptionService,
                clock,
                auditLogger,
                folderRepository
        );
    }

    @Bean
    public UpdatePasswordEntryUseCase updatePasswordEntryUseCase(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            EncryptionService encryptionService,
            Clock clock,
            AuditLogger auditLogger,
            FolderRepository folderRepository
    ) {
        return new UpdatePasswordEntryService(
                passwordEntryRepository,
                userRepository,
                vaultSessionStore,
                encryptionService,
                clock,
                auditLogger,
                folderRepository
        );
    }

    @Bean
    public DeletePasswordEntryUseCase deletePasswordEntryUseCase(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            AuditLogger auditLogger
    ) {
        return new DeletePasswordEntryService(
                passwordEntryRepository,
                userRepository,
                vaultSessionStore,
                auditLogger
        );
    }

    @Bean
    public GetPasswordEntryUseCase getPasswordEntryUseCase(
            PasswordEntryRepository passwordEntryRepository,
            VaultSessionStore vaultSessionStore
    ) {
        return new GetPasswordEntryService(
                passwordEntryRepository,
                vaultSessionStore
        );
    }

    @Bean
    public GetEntriesByUserUseCase getEntriesByUserUseCase(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore
    ) {
        return new GetEntriesByUserService(
                passwordEntryRepository,
                userRepository,
                vaultSessionStore
        );
    }

    @Bean
    public GetEntriesByFolderUseCase getEntriesByFolderUseCase(
            PasswordEntryRepository passwordEntryRepository,
            FolderRepository folderRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore
    ) {
        return new GetEntriesByFolderService(
                passwordEntryRepository,
                folderRepository,
                userRepository,
                vaultSessionStore
        );
    }

    @Bean
    public SearchPasswordEntriesUseCase searchPasswordEntriesUseCase(
            PasswordEntryRepository passwordEntryRepository,
            AutoLockUseCase autoLockUseCase    
        ) {
        return new SearchPasswordEntriesService(
                passwordEntryRepository,
                autoLockUseCase
        );
    }

    @Bean
    public RevealPasswordUseCase revealPasswordUseCase(
            PasswordEntryRepository passwordEntryRepository,
            EncryptionService encryptionService,
            AutoLockUseCase autoLockUseCase,
            AuditLogger auditLogger
    ) {
        return new RevealPasswordService(
                passwordEntryRepository,
                encryptionService,
                autoLockUseCase,
                auditLogger
        );
    }

    @Bean
    public CreateFolderUseCase createFolderUseCase(
            FolderRepository folderRepository,
            UserRepository userRepository,
            AuditLogger auditLogger
    ) {
        return new CreateFolderService(folderRepository, userRepository, auditLogger);
    }

    @Bean
    public RenameFolderUseCase renameFolderUseCase(
            FolderRepository folderRepository,
            UserRepository userRepository,
            AuditLogger auditLogger
    ) {
        return new RenameFolderService(folderRepository, userRepository, auditLogger);
    }

    @Bean
    public DeleteFolderUseCase deleteFolderUseCase(
            FolderRepository folderRepository,
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            AuditLogger auditLogger
    ) {
        return new DeleteFolderService(
                folderRepository,
                passwordEntryRepository,
                userRepository,
                auditLogger
        );
    }

    @Bean
    public GetFolderUseCase getFolderUseCase(FolderRepository folderRepository) {
        return new GetFolderService(folderRepository);
    }

    @Bean
    public GetFoldersByUserUseCase getFoldersByUserUseCase(
            FolderRepository folderRepository,
            UserRepository userRepository
    ) {
        return new GetFoldersByUserService(folderRepository, userRepository);
    }

    @Bean
    public GeneratePasswordUseCase generatePasswordUseCase() {
        return new GeneratePasswordService();
    }

    @Bean
    public GetSecurityActivityUseCase getSecurityActivityUseCase(
            AuditLogRepository auditLogRepository,
            UserRepository userRepository
    ) {
        return new GetSecurityActivityService(auditLogRepository, userRepository);
    }

    @Bean
    public AuthRequestValidator authRequestValidator() {
        return new AuthRequestValidator();
    }

    @Bean
    public FolderRequestValidator folderRequestValidator() {
        return new FolderRequestValidator();
    }

    @Bean
    public PasswordEntryRequestValidator passwordEntryRequestValidator() {
        return new PasswordEntryRequestValidator();
    }

    @Bean
    public AuthDesktopMapper authDesktopMapper() {
        return new AuthDesktopMapper();
    }

    @Bean
    public FolderDesktopMapper folderDesktopMapper() {
        return new FolderDesktopMapper();
    }

    @Bean
    public PasswordEntryDesktopMapper passwordEntryDesktopMapper() {
        return new PasswordEntryDesktopMapper();
    }

    @Bean
    public ErrorDesktopMapper errorDesktopMapper() {
        return new ErrorDesktopMapper();
    }

    @Bean
    public ApiHandler apiHandler(ErrorDesktopMapper errorDesktopMapper) {
        return new ApiHandler(errorDesktopMapper);
    }

    @Bean
    public AuthHandler authHandler(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            ChangeMasterPasswordUseCase changeMasterPasswordUseCase,
            ChangeLoginPasswordUseCase changeLoginPasswordUseCase,
            AuthDesktopMapper authDesktopMapper,
            AuthRequestValidator authRequestValidator,
            ApiHandler apiHandler,
            TokenService tokenService,
            CurrentUserProvider currentUserProvider,
            LogoutUserUseCase logoutUserUseCase,
            RefreshTokenStore refreshTokenStore,
            RefreshAccessTokenUseCase refreshAccessTokenUseCase

    ) {
        return new AuthHandler(
                registerUserUseCase,
                loginUserUseCase,
                changeMasterPasswordUseCase,
                changeLoginPasswordUseCase,
                authDesktopMapper,
                authRequestValidator,
                apiHandler,
                tokenService,
                currentUserProvider,
                logoutUserUseCase,
                refreshTokenStore,
                refreshAccessTokenUseCase

        );
    }

    @Bean
    public PasswordEntryHandler passwordEntryHandler(
            CreatePasswordEntryUseCase createPasswordEntryUseCase,
            GetPasswordEntryUseCase getPasswordEntryUseCase,
            GetEntriesByUserUseCase getEntriesByUserUseCase,
            GetEntriesByFolderUseCase getEntriesByFolderUseCase,
            RevealPasswordUseCase revealPasswordUseCase,
            UpdatePasswordEntryUseCase updatePasswordEntryUseCase,
            DeletePasswordEntryUseCase deletePasswordEntryUseCase,
            PasswordEntryDesktopMapper passwordEntryDesktopMapper,
            PasswordEntryRequestValidator passwordEntryRequestValidator,
            SearchPasswordEntriesUseCase searchPasswordEntriesUseCase,
            ApiHandler apiHandler,
            CurrentUserProvider currentUserProvider
    ) {
        return new PasswordEntryHandler(
                createPasswordEntryUseCase,
                getPasswordEntryUseCase,
                getEntriesByUserUseCase,
                getEntriesByFolderUseCase,
                revealPasswordUseCase,
                updatePasswordEntryUseCase,
                deletePasswordEntryUseCase,
                passwordEntryDesktopMapper,
                passwordEntryRequestValidator,
                searchPasswordEntriesUseCase,
                apiHandler,
                currentUserProvider
                
        );
    }

    @Bean
    public FolderHandler folderHandler(
            CreateFolderUseCase createFolderUseCase,
            GetFolderUseCase getFolderUseCase,
            GetFoldersByUserUseCase getFoldersByUserUseCase,
            RenameFolderUseCase renameFolderUseCase,
            DeleteFolderUseCase deleteFolderUseCase,
            FolderDesktopMapper folderDesktopMapper,
            FolderRequestValidator folderRequestValidator,
            ApiHandler apiHandler,
            CurrentUserProvider currentUserProvider
    ) {
        return new FolderHandler(
                createFolderUseCase,
                getFolderUseCase,
                getFoldersByUserUseCase,
                renameFolderUseCase,
                deleteFolderUseCase,
                folderDesktopMapper,
                folderRequestValidator,
                apiHandler,
                currentUserProvider
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
    public AuditHandler auditHandler(
            GetSecurityActivityUseCase getSecurityActivityUseCase,
            ApiHandler apiHandler,
            CurrentUserProvider currentUserProvider
    ) {
        return new AuditHandler(
                getSecurityActivityUseCase,
                apiHandler,
                currentUserProvider
        );
    }

    @Bean
    public PasswordGeneratorHandler passwordGeneratorHandler(
            GeneratePasswordUseCase generatePasswordUseCase,
            ApiHandler apiHandler
    ) {
        return new PasswordGeneratorHandler(generatePasswordUseCase, apiHandler);
    }

    @Bean
    public AuthController authController(AuthHandler authHandler, ClientIp clientIp) {
        return new AuthController(authHandler, clientIp);
    }

    @Bean
    public PasswordEntryController passwordEntryController(
            PasswordEntryHandler passwordEntryHandler,
            ClientIp clientIp
    ) {
        return new PasswordEntryController(passwordEntryHandler, clientIp);
    }

    @Bean
    public FolderController folderController(FolderHandler folderHandler, ClientIp clientIp) {
        return new FolderController(folderHandler, clientIp);
    }

    @Bean
    public VaultController vaultController(VaultHandler vaultHandler, ClientIp clientIp) {
        return new VaultController(vaultHandler, clientIp);
    }

    @Bean
    public AuditController auditController(AuditHandler auditHandler) {
        return new AuditController(auditHandler);
    }

    @Bean
    public PasswordGeneratorController passwordGeneratorController(PasswordGeneratorHandler passwordGeneratorHandler) {
        return new PasswordGeneratorController(passwordGeneratorHandler);
    }

    @Bean
    public DesktopApiController desktopApiController(
            AuthController authController,
            VaultController vaultController,
            FolderController folderController,
            PasswordEntryController passwordEntryController,
            AuditController auditController,
            PasswordGeneratorController passwordGeneratorController
    ) {
        return new DesktopApiController(
                authController,
                vaultController,
                folderController,
                passwordEntryController,
                auditController,
                passwordGeneratorController
        );
    }
}