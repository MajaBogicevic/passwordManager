package org.service.passwordman.domain.repository;

import org.service.passwordman.domain.model.PasswordEntry;
import java.util.Optional;
import java.util.List;

public interface PasswordEntryRepository {
    Optional<PasswordEntry> findById(int entryId);
    List<PasswordEntry> findByUserId(int userId);
    List<PasswordEntry> findByFolderId(int folderId);
    void save(PasswordEntry entry);
    void deleteById(int entryId);
    List<PasswordEntry> searchByUserIdAndTitle(int userId, String titleQuery);
}