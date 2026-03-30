package org.service.passwordman.domain.repository;

import org.service.passwordman.domain.model.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findById(int userId);
    boolean userExists(String username);
    void save(User user);
}