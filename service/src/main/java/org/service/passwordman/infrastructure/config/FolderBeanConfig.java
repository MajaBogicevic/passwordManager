package org.service.passwordman.infrastructure.config;

import org.service.passwordman.application.port.AuditLogger;
import org.service.passwordman.application.service.folder.CreateFolderService;
import org.service.passwordman.application.service.folder.DeleteFolderService;
import org.service.passwordman.application.service.folder.GetFolderService;
import org.service.passwordman.application.service.folder.GetFoldersByUserService;
import org.service.passwordman.application.service.folder.RenameFolderService;
import org.service.passwordman.application.usecase.folder.CreateFolderUseCase;
import org.service.passwordman.application.usecase.folder.DeleteFolderUseCase;
import org.service.passwordman.application.usecase.folder.GetFolderUseCase;
import org.service.passwordman.application.usecase.folder.GetFoldersByUserUseCase;
import org.service.passwordman.application.usecase.folder.RenameFolderUseCase;
import org.service.passwordman.desktopApi.controller.FolderController;
import org.service.passwordman.desktopApi.handler.ApiHandler;
import org.service.passwordman.desktopApi.handler.FolderHandler;
import org.service.passwordman.desktopApi.mapper.FolderDesktopMapper;
import org.service.passwordman.desktopApi.validation.FolderRequestValidator;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;
import org.service.passwordman.infrastructure.security.ClientIp;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FolderBeanConfig {

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
    public FolderRequestValidator folderRequestValidator() {
        return new FolderRequestValidator();
    }

    @Bean
    public FolderDesktopMapper folderDesktopMapper() {
        return new FolderDesktopMapper();
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
    public FolderController folderController(FolderHandler folderHandler, ClientIp clientIp) {
        return new FolderController(folderHandler, clientIp);
    }
}