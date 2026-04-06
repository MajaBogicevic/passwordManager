package org.service.passwordman.infrastructure.persistence.repository;

import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPasswordEntryRepository implements PasswordEntryRepository {

    private final Map<Integer, PasswordEntry> entriesById = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Optional<PasswordEntry> findById(int entryId) {
        return Optional.ofNullable(entriesById.get(entryId));
    }

    @Override
    public List<PasswordEntry> findByUserId(int userId) {
        return entriesById.values()
                .stream()
                .filter(entry -> entry.getUserId() == userId)
                .toList();
    }

    @Override
    public List<PasswordEntry> findByFolderId(int folderId) {
        return entriesById.values()
                .stream()
                .filter(entry -> entry.getFolderId() == folderId)
                .toList();
    }

    @Override
    public void save(PasswordEntry entry) {
        PasswordEntry entryToStore = entry;

        if (entry.getId() == 0) {
            int newId = idGenerator.getAndIncrement();
            entryToStore = new PasswordEntry(
                    newId,
                    entry.getUserId(),
                    entry.getTitle(),
                    entry.getUrl(),
                    entry.getUsername(),
                    entry.getEncryptedPassword(),
                    entry.getNotes(),
                    entry.getFolderId(),
                    entry.getCreatedAt(),
                    entry.getUpdatedAt()
            );
        }

        entriesById.put(entryToStore.getId(), entryToStore);
    }

    @Override
    public void deleteById(int entryId) {
        entriesById.remove(entryId);
    }

    @Override
    public List<PasswordEntry> searchByUserIdAndTitle(int userId, String titleQuery) {
        String normalizedQuery = titleQuery.trim().toLowerCase();

        if (normalizedQuery.isEmpty()) {
            return findByUserId(userId);
        }

        return entriesById.values().stream().filter(entry -> entry.getUserId() == userId).filter(entry -> entry.getTitle() != null).filter(entry -> entry.getTitle().toLowerCase().contains(normalizedQuery)).toList();
        }

}