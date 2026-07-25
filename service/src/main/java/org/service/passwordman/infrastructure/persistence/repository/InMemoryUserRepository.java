package org.service.passwordman.infrastructure.persistence.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.service.passwordman.domain.model.User;
import org.service.passwordman.domain.repository.UserRepository;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Integer, User> usersById = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Optional<User> findByUsername(String username) {
        return usersById.values()
                .stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findById(int userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    @Override
    public boolean existsByUsername(String username) {
        return usersById.values()
                .stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public void save(User user) {
        User userToStore = user;

        if (user.getId() == 0) {
            int newId = idGenerator.getAndIncrement();
            userToStore = new User(
                    newId,
                    user.getEmail(),
                    user.getUsername(),
                    user.getPasswordHash(),
                    user.getKeySalt(),
                    user.getWrappedDataKey(),
                    user.getNotes(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }

        usersById.put(userToStore.getId(), userToStore);
    }
}