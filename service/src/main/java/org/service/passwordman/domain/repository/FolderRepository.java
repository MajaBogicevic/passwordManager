package org.service.passwordman.domain.repository;

import org.service.passwordman.domain.model.Folder;
import java.util.Optional;
import java.util.List;

public interface FolderRepository {
    Optional<Folder> findById(int folderId);
    List<Folder> findByUserId(int userId);
    void save(Folder folder);
    void deleteById(int folderId);
}