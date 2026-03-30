package org.service.passwordman.domain.repository;

public interface VaultSessionRepository {
    void unlock(int userId);
    void lock(int userId);
    boolean isUnlocked(int userId);
}