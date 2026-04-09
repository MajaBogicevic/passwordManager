package org.service.passwordman.infrastructure.persistence.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.domain.repository.PasswordEntryRepository;

public class InMemoryPasswordEntryRepository implements PasswordEntryRepository {

    private final Map<Integer, PasswordEntry> entriesById = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Optional<PasswordEntry> findById(int entryId) {
        return Optional.ofNullable(entriesById.get(entryId));
    }

    @Override
    public Optional<PasswordEntry> findByIdAndUserId(int entryId, int userId) {
        PasswordEntry entry = entriesById.get(entryId);

        if (entry == null || entry.getUserId() != userId) {
            return Optional.empty();
        }

        return Optional.of(entry);
    }

    @Override
    public List<PasswordEntry> findByUserId(int userId) {
        return entriesById.values()
                .stream()
                .filter(entry -> entry.getUserId() == userId)
                .toList();
    }

    @Override
    public List<PasswordEntry> findByFolderIdAndUserId(int folderId, int userId) {
        return entriesById.values()
                .stream()
                .filter(entry -> entry.getFolderId() == folderId && entry.getUserId() == userId)
                .toList();
    }

    @Override
    public void save(PasswordEntry passwordEntry) {
        PasswordEntry entryToStore = passwordEntry;

        if (passwordEntry.getId() == 0) {
            int newId = idGenerator.getAndIncrement();

            entryToStore = new PasswordEntry(
                    newId,
                    passwordEntry.getUserId(),
                    passwordEntry.getTitle(),
                    passwordEntry.getUrl(),
                    passwordEntry.getUsername(),
                    passwordEntry.getEncryptedPassword(),
                    passwordEntry.getNotes(),
                    passwordEntry.getFolderId(),
                    passwordEntry.getCreatedAt(),
                    passwordEntry.getUpdatedAt()
            );
        }

        entriesById.put(entryToStore.getId(), entryToStore);
    }

    @Override
    public void deleteById(int entryId) {
        entriesById.remove(entryId);
    }

    @Override
    public boolean deleteByIdAndUserId(int entryId, int userId) {
        PasswordEntry entry = entriesById.get(entryId);

        if (entry == null || entry.getUserId() != userId) {
            return false;
        }

        return entriesById.remove(entryId, entry);
    }

    @Override
    public List<PasswordEntry> searchByUserIdAndTitle(int userId, String titleQuery) {
        String normalizedQuery = titleQuery.trim().toLowerCase();

        return entriesById.values()
                .stream()
                .filter(entry -> entry.getUserId() == userId)
                .filter(entry -> {
                    String title = entry.getTitle();
                    return title != null && title.toLowerCase().contains(normalizedQuery);
                })
                .toList();
    }
}