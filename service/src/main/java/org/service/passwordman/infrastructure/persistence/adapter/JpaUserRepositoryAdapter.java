package org.service.passwordman.infrastructure.persistence.adapter;

import java.util.Optional;

import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;
import org.service.passwordman.infrastructure.persistence.entity.UserEntity;
import org.service.passwordman.infrastructure.persistence.jpa.SpringDataUserJpaRepository;

public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserJpaRepository repository;

    public JpaUserRepositoryAdapter(SpringDataUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(int userId) {
        return repository.findById(userId).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public void save(User user) {
        repository.save(toEntity(user));
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getUsername(),
                entity.getLoginPasswordHash(),
                entity.getMasterPasswordHash(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        if (user.getId() > 0) {
            entity.setId(user.getId());
        }
        entity.setEmail(user.getEmail());
        entity.setUsername(user.getUsername());
        entity.setLoginPasswordHash(user.getLoginPasswordHash());
        entity.setMasterPasswordHash(user.getMasterPasswordHash());
        entity.setNotes(user.getNotes());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
}