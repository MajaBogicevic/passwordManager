package org.service.passwordman.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.service.passwordman.domain.model.Folder;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.infrastructure.persistence.entity.FolderEntity;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataFolderJpaRepository;

public class JpaFolderRepositoryAdapter implements FolderRepository {

    private final SpringDataFolderJpaRepository repository;

    public JpaFolderRepositoryAdapter(SpringDataFolderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Folder> findById(int folderId) {
        return repository.findById(folderId).map(this::toDomain);
    }

    @Override
    public Optional<Folder> findByIdAndUserId(int folderId, int userId) {
        return repository.findByIdAndUserId(folderId, userId).map(this::toDomain);
    }

    @Override
    public List<Folder> findByUserId(int userId) {
        return repository.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public Folder save(Folder folder) {
        FolderEntity savedEntity = repository.save(toEntity(folder));
        return toDomain(savedEntity);
    }

    @Override
    public void deleteById(int folderId) {
        repository.deleteById(folderId);
    }

    @Override
    public boolean deleteByIdAndUserId(int folderId, int userId) {
        return repository.deleteByIdAndUserId(folderId, userId) > 0;
    }

    private Folder toDomain(FolderEntity entity) {
        return new Folder(
                entity.getId(),
                entity.getUserId(),
                entity.getName()
        );
    }

    private FolderEntity toEntity(Folder folder) {
        FolderEntity entity = new FolderEntity();
        if (folder.getId() > 0) {
            entity.setId(folder.getId());
        }
        entity.setUserId(folder.getUserId());
        entity.setName(folder.getName());
        return entity;
    }
}