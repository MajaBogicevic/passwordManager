package org.service.passwordman.infrastructure.persistence.repository;

import org.service.passwordman.domain.model.Folder;
import org.service.passwordman.domain.repository.FolderRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void save(Folder folder) {
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
    }

    @Override
    public void deleteById(int folderId) {
        foldersById.remove(folderId);
    }
}