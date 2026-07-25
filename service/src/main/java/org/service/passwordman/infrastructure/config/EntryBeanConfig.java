package org.service.passwordman.infrastructure.config;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.port.Clock;
import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.application.port.VaultKeyStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.application.service.entry.CreatePasswordEntryService;
import org.service.passwordman.application.service.entry.DeletePasswordEntryService;
import org.service.passwordman.application.service.entry.GetEntriesByFolderService;
import org.service.passwordman.application.service.entry.GetEntriesByUserService;
import org.service.passwordman.application.service.entry.GetPasswordEntryService;
import org.service.passwordman.application.service.entry.RevealPasswordService;
import org.service.passwordman.application.service.entry.SearchPasswordEntriesService;
import org.service.passwordman.application.service.entry.UpdatePasswordEntryService;
import org.service.passwordman.application.usecase.entry.CreatePasswordEntryUseCase;
import org.service.passwordman.application.usecase.entry.DeletePasswordEntryUseCase;
import org.service.passwordman.application.usecase.entry.GetEntriesByFolderUseCase;
import org.service.passwordman.application.usecase.entry.GetEntriesByUserUseCase;
import org.service.passwordman.application.usecase.entry.GetPasswordEntryUseCase;
import org.service.passwordman.application.usecase.entry.RevealPasswordUseCase;
import org.service.passwordman.application.usecase.entry.SearchPasswordEntriesUseCase;
import org.service.passwordman.application.usecase.entry.UpdatePasswordEntryUseCase;
import org.service.passwordman.application.usecase.vault.AutoLockUseCase;
import org.service.passwordman.desktopApi.controller.PasswordEntryController;
import org.service.passwordman.desktopApi.handler.ApiHandler;
import org.service.passwordman.desktopApi.handler.PasswordEntryHandler;
import org.service.passwordman.desktopApi.mapper.PasswordEntryDesktopMapper;
import org.service.passwordman.desktopApi.validation.PasswordEntryRequestValidator;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;
import org.service.passwordman.infrastructure.security.ClientIp;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntryBeanConfig {

    @Bean
    public CreatePasswordEntryUseCase createPasswordEntryUseCase(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService,
            Clock clock,
            AuditLogger auditLogger,
            FolderRepository folderRepository
    ) {
        return new CreatePasswordEntryService(
                passwordEntryRepository,
                userRepository,
                vaultSessionStore,
                vaultKeyStore,
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
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService,
            Clock clock,
            AuditLogger auditLogger,
            FolderRepository folderRepository
    ) {
        return new UpdatePasswordEntryService(
                passwordEntryRepository,
                userRepository,
                vaultSessionStore,
                vaultKeyStore,
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
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService
    ) {
        return new GetPasswordEntryService(
                passwordEntryRepository,
                vaultSessionStore,
                vaultKeyStore,
                encryptionService
        );
    }

    @Bean
    public GetEntriesByUserUseCase getEntriesByUserUseCase(
            PasswordEntryRepository passwordEntryRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService
    ) {
        return new GetEntriesByUserService(
                passwordEntryRepository,
                userRepository,
                vaultSessionStore,
                vaultKeyStore,
                encryptionService
        );
    }

    @Bean
    public GetEntriesByFolderUseCase getEntriesByFolderUseCase(
            PasswordEntryRepository passwordEntryRepository,
            FolderRepository folderRepository,
            UserRepository userRepository,
            VaultSessionStore vaultSessionStore,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService
    ) {
        return new GetEntriesByFolderService(
                passwordEntryRepository,
                folderRepository,
                userRepository,
                vaultSessionStore,
                vaultKeyStore,
                encryptionService
        );
    }

    @Bean
    public SearchPasswordEntriesUseCase searchPasswordEntriesUseCase(
            PasswordEntryRepository passwordEntryRepository,
            AutoLockUseCase autoLockUseCase,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService
    ) {
        return new SearchPasswordEntriesService(
                passwordEntryRepository,
                autoLockUseCase,
                vaultKeyStore,
                encryptionService
        );
    }

    @Bean
    public RevealPasswordUseCase revealPasswordUseCase(
            PasswordEntryRepository passwordEntryRepository,
            VaultKeyStore vaultKeyStore,
            EncryptionService encryptionService,
            AutoLockUseCase autoLockUseCase,
            AuditLogger auditLogger
    ) {
        return new RevealPasswordService(
                passwordEntryRepository,
                vaultKeyStore,
                encryptionService,
                autoLockUseCase,
                auditLogger
        );
    }

    @Bean
    public PasswordEntryRequestValidator passwordEntryRequestValidator() {
        return new PasswordEntryRequestValidator();
    }

    @Bean
    public PasswordEntryDesktopMapper passwordEntryDesktopMapper() {
        return new PasswordEntryDesktopMapper();
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
    public PasswordEntryController passwordEntryController(
            PasswordEntryHandler passwordEntryHandler,
            ClientIp clientIp
    ) {
        return new PasswordEntryController(passwordEntryHandler, clientIp);
    }
}