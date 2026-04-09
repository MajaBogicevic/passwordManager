package org.service.passwordman.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.service.passwordman.infrastructure.persistence.entity.PasswordEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPasswordEntryJpaRepository extends JpaRepository<PasswordEntryEntity, Integer> {
    Optional<PasswordEntryEntity> findByIdAndUserId(int id, int userId);
    List<PasswordEntryEntity> findByUserId(int userId);
    List<PasswordEntryEntity> findByFolderIdAndUserId(int folderId, int userId);
    List<PasswordEntryEntity> findByUserIdAndTitleContainingIgnoreCase(int userId, String titleQuery);
    long deleteByIdAndUserId(int id, int userId);
}