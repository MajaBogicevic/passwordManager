package org.service.passwordman.infrastructure.config;

import org.service.passwordman.application.service.audit.GetSecurityActivityService;
import org.service.passwordman.application.service.generator.GeneratePasswordService;
import org.service.passwordman.application.usecase.audit.GetSecurityActivityUseCase;
import org.service.passwordman.application.usecase.generator.GeneratePasswordUseCase;
import org.service.passwordman.desktopApi.controller.AuditController;
import org.service.passwordman.desktopApi.controller.AuthController;
import org.service.passwordman.desktopApi.controller.DesktopApiController;
import org.service.passwordman.desktopApi.controller.FolderController;
import org.service.passwordman.desktopApi.controller.PasswordEntryController;
import org.service.passwordman.desktopApi.controller.PasswordGeneratorController;
import org.service.passwordman.desktopApi.controller.VaultController;
import org.service.passwordman.desktopApi.handler.ApiHandler;
import org.service.passwordman.desktopApi.handler.AuditHandler;
import org.service.passwordman.desktopApi.handler.PasswordGeneratorHandler;
import org.service.passwordman.desktopApi.mapper.ErrorDesktopMapper;
import org.service.passwordman.domain.repository.AuditLogRepository;
import org.service.passwordman.domain.repository.UserRepository;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MiscBeanConfig {

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
    public ErrorDesktopMapper errorDesktopMapper() {
        return new ErrorDesktopMapper();
    }

    @Bean
    public ApiHandler apiHandler(ErrorDesktopMapper errorDesktopMapper) {
        return new ApiHandler(errorDesktopMapper);
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