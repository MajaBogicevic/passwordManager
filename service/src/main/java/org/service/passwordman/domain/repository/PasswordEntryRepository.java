package org.service.passwordman.domain.repository;

import java.util.List;
import java.util.Optional;

import org.service.passwordman.domain.model.PasswordEntry;

public interface PasswordEntryRepository {

    Optional<PasswordEntry> findById(int entryId);

    Optional<PasswordEntry> findByIdAndUserId(int entryId, int userId);

    List<PasswordEntry> findByUserId(int userId);

    List<PasswordEntry> findByFolderIdAndUserId(int folderId, int userId);

    List<PasswordEntry> searchByUserIdAndTitle(int userId, String titleQuery);

    void save(PasswordEntry passwordEntry);

    void deleteById(int entryId);

    boolean deleteByIdAndUserId(int entryId, int userId);
}