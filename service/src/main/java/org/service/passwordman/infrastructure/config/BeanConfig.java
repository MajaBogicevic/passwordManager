package org.service.passwordman.infrastructure.config;

import java.time.Duration;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.PasswordHasher;
import org.service.passwordman.application.port.TokenService;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.service.audit.GetSecurityActivityService;
import org.service.passwordman.application.service.auth.ChangeLoginPasswordService;
import org.service.passwordman.application.service.auth.ChangeMasterPasswordService;
import org.service.passwordman.application.service.auth.LoginUserService;
import org.service.passwordman.application.service.auth.RegisterUserService;
import org.service.passwordman.application.service.entry.CopyPasswordService;
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
import org.service.passwordman.application.usecase.auth.RegisterUserUseCase;
import org.service.passwordman.application.usecase.entry.CopyPasswordUseCase;
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
import org.service.passwordman.infrastructure.persistence.repository.InMemoryAuditLogRepository;
import org.service.passwordman.infrastructure.persistence.repository.InMemoryFolderRepository;
import org.service.passwordman.infrastructure.persistence.repository.InMemoryPasswordEntryRepository;
import org.service.passwordman.infrastructure.persistence.repository.InMemoryUserRepository;
import org.service.passwordman.infrastructure.security.BCryptPasswordHasher;
import org.service.passwordman.infrastructure.security.JwtTokenService;
import org.service.passwordman.infrastructure.session.InMemoryVaultSessionStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PasswordmanProperties.class)
public class BeanConfig {

    @Bean
    public TokenService tokenService(PasswordmanProperties properties) {
        return new JwtTokenService(
                properties.getJwtSecret(),
                properties.getJwtExpirationMillis()
        );
    }

    @Bean
    public Clock clock() {
        return new SystemClockAdapter();
    }

    @Bean
    public PasswordHasher passwordHasher() {
        return new BCryptPasswordHasher();
    }

    @Bean
    public EncryptionService encryptionService(PasswordmanProperties properties) {
        return new CryptPasswordEncryptionAdapter(
                properties.getEncryptionKeyRef(),
                properties.getEncryptionMasterKeyBase64()
        );
    }

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Bean
    public PasswordEntryRepository passwordEntryRepository() {
        return new InMemoryPasswordEntryRepository();
    }

    @Bean
    public FolderRepository folderRepository() {
        return new InMemoryFolderRepository();
    }

    @Bean
    public AuditLogRepository auditLogRepository() {
        return new InMemoryAuditLogRepository();
    }

    @Bean
    public AuditLogger auditLogger(AuditLogRepository auditLogRepository, Clock clock) {
        return new AuditLoggerAdapter(auditLogRepository, clock);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            Clock clock
    ) {
        return new RegisterUserService(userRepository, passwordHasher, clock);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AuditLogger auditLogger
    ) {
        return new LoginUserService(userRepository, passwordHasher, auditLogger);
    }

    @Bean
    public UnlockVaultUseCase unlockVaultUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            VaultSessionStore vaultSessionStore,
            AuditLogger auditLogger,
            Clock clock
    ) {
        return new UnlockVaultService(userRepository, passwordHasher, vaultSessionStore, auditLogger, clock);
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
            Clock clock,
            AuditLogger auditLogger
    ) {
        return new AutoLockService(vaultSessionStore, clock ,auditLogger, Duration.ofMinutes(5));
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
    public CreatePasswordEntryUseCase createPasswordEntryUseCase(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            EncryptionService encryptionService,
            Clock clock,
            AuditLogger auditLogger
    ) {
        return new CreatePasswordEntryService(
                passwordEntryRepository,
                userRepository,
                vaultSessionStore,
                encryptionService,
                clock,
                auditLogger
        );
    }

    @Bean
    public GetPasswordEntryUseCase getPasswordEntryUseCase(
            PasswordEntryRepository passwordEntryRepository,
            VaultSessionStore vaultSessionStore
    ) {
        return new GetPasswordEntryService(passwordEntryRepository, vaultSessionStore);
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
        public SearchPasswordEntriesUseCase searchPasswordEntriesUseCase(
                PasswordEntryRepository passwordEntryRepository
        ) {
            return new SearchPasswordEntriesService(passwordEntryRepository);
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
    public CopyPasswordUseCase copyPasswordUseCase(
            PasswordEntryRepository passwordEntryRepository,
            EncryptionService encryptionService,
            AutoLockUseCase autoLockUseCase,
            AuditLogger auditLogger
    ) {
        return new CopyPasswordService(
                passwordEntryRepository,
                encryptionService,
                autoLockUseCase,
                auditLogger
        );
    }

    @Bean
    public UpdatePasswordEntryUseCase updatePasswordEntryUseCase(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            EncryptionService encryptionService,
            Clock clock,
            AuditLogger auditLogger
    ) {
        return new UpdatePasswordEntryService(
                passwordEntryRepository,
                userRepository,
                vaultSessionStore,
                encryptionService,
                clock,
                auditLogger
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
    public GetSecurityActivityUseCase getSecurityActivityUseCase(
            AuditLogRepository auditLogRepository,
            UserRepository userRepository
    ) {
        return new GetSecurityActivityService(auditLogRepository, userRepository);
    }

    @Bean
    public GeneratePasswordUseCase generatePasswordUseCase() {
        return new GeneratePasswordService();
    }

    @Bean
    public ChangeMasterPasswordUseCase changeMasterPasswordUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            VaultSessionStore vaultSessionStore,
            AuditLogger auditLogger,
            Clock clock
    ) {
        return new ChangeMasterPasswordService(
                userRepository,
                passwordHasher,
                vaultSessionStore,
                auditLogger,
                clock
        );
    }

    @Bean 
    public ChangeLoginPasswordUseCase changeLoginPasswordUseCase(
        UserRepository userRepository,
        PasswordHasher passwordHasher,
        AuditLogger auditLogger,
        Clock clock
    ) {
        return new ChangeLoginPasswordService(
                userRepository,
                passwordHasher,
                auditLogger,
                clock
        );
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
    public AuthHandler authHandler(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            ChangeMasterPasswordUseCase changeMasterPasswordUseCase,
            ChangeLoginPasswordUseCase changeLoginPasswordUseCase,
            AuthDesktopMapper authDesktopMapper,
            AuthRequestValidator authRequestValidator,
            ApiHandler apiHandler
    ) {
        return new AuthHandler(
                registerUserUseCase,
                loginUserUseCase,
                changeMasterPasswordUseCase,
                changeLoginPasswordUseCase,
                authDesktopMapper,
                authRequestValidator,
                apiHandler
        );
    }

    @Bean
    public VaultHandler vaultHandler(
            UnlockVaultUseCase unlockVaultUseCase,
            LockVaultUseCase lockVaultUseCase,
            AutoLockUseCase autoLockUseCase,
            AuthDesktopMapper authDesktopMapper,
            AuthRequestValidator authRequestValidator,
            ApiHandler apiHandler
    ) {
        return new VaultHandler(
                unlockVaultUseCase,
                lockVaultUseCase,
                autoLockUseCase,
                authDesktopMapper,
                authRequestValidator,
                apiHandler
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
            ApiHandler apiHandler
    ) {
        return new FolderHandler(
                createFolderUseCase,
                getFolderUseCase,
                getFoldersByUserUseCase,
                renameFolderUseCase,
                deleteFolderUseCase,
                folderDesktopMapper,
                folderRequestValidator,
                apiHandler
        );
    }

    @Bean
    public PasswordEntryHandler passwordEntryHandler(
            CreatePasswordEntryUseCase createPasswordEntryUseCase,
            GetPasswordEntryUseCase getPasswordEntryUseCase,
            GetEntriesByUserUseCase getEntriesByUserUseCase,
            GetEntriesByFolderUseCase getEntriesByFolderUseCase,
            RevealPasswordUseCase revealPasswordUseCase,
            CopyPasswordUseCase copyPasswordUseCase,
            UpdatePasswordEntryUseCase updatePasswordEntryUseCase,
            DeletePasswordEntryUseCase deletePasswordEntryUseCase,
            PasswordEntryDesktopMapper passwordEntryDesktopMapper,
            PasswordEntryRequestValidator passwordEntryRequestValidator,
            SearchPasswordEntriesUseCase searchPasswordEntriesUseCase,
            ApiHandler apiHandler
    ) {
        return new PasswordEntryHandler(
                createPasswordEntryUseCase,
                getPasswordEntryUseCase,
                getEntriesByUserUseCase,
                getEntriesByFolderUseCase,
                revealPasswordUseCase,
                copyPasswordUseCase,
                updatePasswordEntryUseCase,
                deletePasswordEntryUseCase,
                passwordEntryDesktopMapper,
                passwordEntryRequestValidator,
                searchPasswordEntriesUseCase,
                apiHandler
        );
    }

    @Bean
    public AuditHandler auditHandler(
            GetSecurityActivityUseCase getSecurityActivityUseCase,
            ApiHandler apiHandler
    ) {
        return new AuditHandler(getSecurityActivityUseCase, apiHandler);
    }

    @Bean
    public PasswordGeneratorHandler passwordGeneratorHandler(
            GeneratePasswordUseCase generatePasswordUseCase,
            ApiHandler apiHandler
    ) {
        return new PasswordGeneratorHandler(generatePasswordUseCase, apiHandler);
    }

    @Bean
    public AuthController authController(AuthHandler authHandler) {
        return new AuthController(authHandler);
    }

    @Bean
    public VaultController vaultController(VaultHandler vaultHandler) {
        return new VaultController(vaultHandler);
    }

    @Bean
    public FolderController folderController(FolderHandler folderHandler) {
        return new FolderController(folderHandler);
    }

    @Bean
    public PasswordEntryController passwordEntryController(PasswordEntryHandler passwordEntryHandler) {
        return new PasswordEntryController(passwordEntryHandler);
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

    @Bean
    public AuditController auditController(AuditHandler auditHandler) {
        return new AuditController(auditHandler);
    }

    @Bean
    public PasswordGeneratorController passwordGeneratorController(
            PasswordGeneratorHandler passwordGeneratorHandler
    ) {
        return new PasswordGeneratorController(passwordGeneratorHandler);
    }

    @Bean
    public VaultSessionStore vaultSessionStore() {
        return new InMemoryVaultSessionStore();
    }

    @Bean
    public AutoLockService autoLockService(
            VaultSessionStore vaultSessionStore,
            Clock clock,
            AuditLogger auditLogger
    ) {
        return new AutoLockService(
                vaultSessionStore,
                clock,
                auditLogger,
                Duration.ofMinutes(5)
        );
    }
}