package org.service.passwordman.application.service.folder;

import org.service.passwordman.application.usecase.folder.GetFoldersByUserUseCase;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.model.Folder;
import org.service.passwordman.domain.repository.FolderRepository;
import org.service.passwordman.domain.repository.UserRepository;

import java.util.List;

public class GetFoldersByUserService implements GetFoldersByUserUseCase {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public GetFoldersByUserService(
            FolderRepository folderRepository,
            UserRepository userRepository
    ) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Folder> execute(int userId) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        return folderRepository.findByUserId(userId);
    }
}