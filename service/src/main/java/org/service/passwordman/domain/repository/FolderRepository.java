package org.service.passwordman.domain.repository;

import java.util.List;
import java.util.Optional;

import org.service.passwordman.domain.model.Folder;

public interface FolderRepository {
    Optional<Folder> findById(int folderId);
    Optional<Folder> findByIdAndUserId(int folderId, int userId);
    List<Folder> findByUserId(int userId);
    Folder save(Folder folder);
    void deleteById(int folderId);

    boolean deleteByIdAndUserId(int folderId, int userId);
}