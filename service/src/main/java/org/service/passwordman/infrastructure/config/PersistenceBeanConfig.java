package org.service.passwordman.infrastructure.config;

import org.service.passwordman.application.port.RefreshTokenStore;
import org.service.passwordman.application.port.VaultSessionStore;
import org.service.passwordman.domain.repository.AuditLogRepository;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.domain.repository.UserRepository;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceBeanConfig {

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
    public RefreshTokenStore refreshTokenStore(SpringDataRefreshTokenJpaRepository repository) {
        return new JpaRefreshTokenStoreAdapter(repository);
    }
}