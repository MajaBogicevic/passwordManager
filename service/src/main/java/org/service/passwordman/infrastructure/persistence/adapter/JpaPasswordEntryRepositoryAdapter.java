package org.service.passwordman.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;
import org.service.passwordman.infrastructure.persistence.entity.PasswordEntryEntity;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataPasswordEntryJpaRepository;

public class JpaPasswordEntryRepositoryAdapter implements PasswordEntryRepository {

    private final SpringDataPasswordEntryJpaRepository repository;

    public JpaPasswordEntryRepositoryAdapter(SpringDataPasswordEntryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PasswordEntry> findById(int entryId) {
        return repository.findById(entryId).map(this::toDomain);
    }

    @Override
    public Optional<PasswordEntry> findByIdAndUserId(int entryId, int userId) {
        return repository.findByIdAndUserId(entryId, userId).map(this::toDomain);
    }

    @Override
    public List<PasswordEntry> findByUserId(int userId) {
        return repository.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PasswordEntry> findByFolderIdAndUserId(int folderId, int userId) {
        return repository.findByFolderIdAndUserId(folderId, userId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PasswordEntry> searchByUserIdAndTitle(int userId, String titleQuery) {
        return repository.findByUserIdAndTitleContainingIgnoreCase(userId, titleQuery).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void save(PasswordEntry passwordEntry) {
        repository.save(toEntity(passwordEntry));
    }

    @Override
    public void deleteById(int entryId) {
        repository.deleteById(entryId);
    }

    @Override
    public boolean deleteByIdAndUserId(int entryId, int userId) {
        return repository.deleteByIdAndUserId(entryId, userId) > 0;
    }

    private PasswordEntry toDomain(PasswordEntryEntity entity) {
        return new PasswordEntry(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getUrl(),
                entity.getUsername(),
                entity.getEncryptedPassword(),
                entity.getNotes(),
                entity.getFolderId() == null ? 0 : entity.getFolderId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private PasswordEntryEntity toEntity(PasswordEntry entry) {
        PasswordEntryEntity entity = new PasswordEntryEntity();
        if (entry.getId() > 0) {
            entity.setId(entry.getId());
        }
        entity.setUserId(entry.getUserId());
        entity.setTitle(entry.getTitle());
        entity.setUrl(entry.getUrl());
        entity.setUsername(entry.getUsername());
        entity.setEncryptedPassword(entry.getEncryptedPassword());
        entity.setNotes(entry.getNotes());
        entity.setFolderId(entry.getFolderId() <= 0 ? null : entry.getFolderId());
        entity.setCreatedAt(entry.getCreatedAt());
        entity.setUpdatedAt(entry.getUpdatedAt());
        return entity;
    }
}