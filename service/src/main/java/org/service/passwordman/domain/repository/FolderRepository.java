package org.service.passwordman.domain.repository;

import org.service.passwordman.domain.model.Folder;
import java.util.Optional;
import java.util.List;

public interface FolderRepository {
    Optional<Folder> findById(int folderId);
    Optional<Folder> findByIdAndUserId(int folderId, int userId);
    List<Folder> findByUserId(int userId);
    void save(Folder folder);
    void deleteById(int folderId);
}