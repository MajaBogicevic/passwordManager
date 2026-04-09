package org.service.passwordman.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.service.passwordman.infrastructure.persistence.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataFolderJpaRepository extends JpaRepository<FolderEntity, Integer> {
    List<FolderEntity> findByUserId(int userId);
    Optional<FolderEntity> findByIdAndUserId(int id, int userId);
    long deleteByIdAndUserId(int id, int userId);
}