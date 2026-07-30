package org.service.passwordman.infrastructure.persistence.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.service.passwordman.domain.model.Folder;
import org.service.passwordman.domain.repository.FolderRepository;

public class InMemoryFolderRepository implements FolderRepository {

    private final Map<Integer, Folder> foldersById = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Optional<Folder> findById(int folderId) {
        return Optional.ofNullable(foldersById.get(folderId));
    }

    @Override
    public List<Folder> findByUserId(int userId) {
        return foldersById.values()
                .stream()
                .filter(folder -> folder.getUserId() == userId)
                .toList();
    }

    @Override
    public Optional<Folder> findByIdAndUserId(int folderId, int userId) {
        Folder folder = foldersById.get(folderId);

        if (folder == null || folder.getUserId() != userId) {
            return Optional.empty();
        }

        return Optional.of(folder);
    }

    @Override
    public Folder save(Folder folder) {
        Folder folderToStore = folder;

        if (folder.getId() == 0) {
            int newId = idGenerator.getAndIncrement();
            folderToStore = new Folder(
                    newId,
                    folder.getUserId(),
                    folder.getName()
            );
        }

        foldersById.put(folderToStore.getId(), folderToStore);
        return folderToStore;
    }

    @Override
    public void deleteById(int folderId) {
        foldersById.remove(folderId);
    }

    @Override
    public boolean deleteByIdAndUserId(int folderId, int userId) {
        Folder folder = foldersById.get(folderId);

        if (folder == null || folder.getUserId() != userId) {
            return false;
        }

        return foldersById.remove(folderId, folder);
    }
}