package org.service.passwordman.desktopApi.handler;

import org.service.passwordman.application.usecase.folder.CreateFolderUseCase;
import org.service.passwordman.application.usecase.folder.DeleteFolderUseCase;
import org.service.passwordman.application.usecase.folder.GetFolderUseCase;
import org.service.passwordman.application.usecase.folder.GetFoldersByUserUseCase;
import org.service.passwordman.application.usecase.folder.RenameFolderUseCase;
import org.service.passwordman.desktopApi.mapper.FolderDesktopMapper;
import org.service.passwordman.desktopApi.request.CreateFolderRequest;
import org.service.passwordman.desktopApi.request.RenameFolderRequest;
import org.service.passwordman.desktopApi.response.AuthResponse;
import org.service.passwordman.desktopApi.response.FolderResponse;
import org.service.passwordman.desktopApi.validation.FolderRequestValidator;
import org.service.passwordman.domain.model.Folder;

import java.util.List;
import java.util.stream.Collectors;

public class FolderHandler {

    private final CreateFolderUseCase createFolderUseCase;
    private final GetFolderUseCase getFolderUseCase;
    private final GetFoldersByUserUseCase getFoldersByUserUseCase;
    private final RenameFolderUseCase renameFolderUseCase;
    private final DeleteFolderUseCase deleteFolderUseCase;
    private final FolderDesktopMapper folderDesktopMapper;
    private final FolderRequestValidator folderRequestValidator;
    private final ApiHandler apiHandler;

    public FolderHandler(
            CreateFolderUseCase createFolderUseCase,
            GetFolderUseCase getFolderUseCase,
            GetFoldersByUserUseCase getFoldersByUserUseCase,
            RenameFolderUseCase renameFolderUseCase,
            DeleteFolderUseCase deleteFolderUseCase,
            FolderDesktopMapper folderDesktopMapper,
            FolderRequestValidator folderRequestValidator,
            ApiHandler apiHandler
    ) {
        this.createFolderUseCase = createFolderUseCase;
        this.getFolderUseCase = getFolderUseCase;
        this.getFoldersByUserUseCase = getFoldersByUserUseCase;
        this.renameFolderUseCase = renameFolderUseCase;
        this.deleteFolderUseCase = deleteFolderUseCase;
        this.folderDesktopMapper = folderDesktopMapper;
        this.folderRequestValidator = folderRequestValidator;
        this.apiHandler = apiHandler;
    }

    public AuthResponse create(CreateFolderRequest request) {
        folderRequestValidator.validateCreate(request);
        createFolderUseCase.execute(request.getUserId(), request.getFolderName());
        return new AuthResponse(true, "Folder successfully created.");
    }

    public Object createSafe(CreateFolderRequest request) {
        return apiHandler.execute(() -> create(request));
    }

    public FolderResponse get(int folderId) {
        Folder folder = getFolderUseCase.execute(folderId);
        return folderDesktopMapper.toResponse(folder);
    }

    public Object getSafe(int folderId) {
        return apiHandler.execute(() -> get(folderId));
    }

    public List<FolderResponse> getByUser(int userId) {
        return getFoldersByUserUseCase.execute(userId)
                .stream()
                .map(folderDesktopMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Object getByUserSafe(int userId) {
        return apiHandler.execute(() -> getByUser(userId));
    }

    public AuthResponse rename(RenameFolderRequest request) {
        folderRequestValidator.validateRename(request);

        renameFolderUseCase.execute(
                request.getUserId(),
                request.getFolderId(),
                request.getNewName()
        );

        return new AuthResponse(true, "Folder successfully renamed.");
    }

    public Object renameSafe(RenameFolderRequest request) {
        return apiHandler.execute(() -> rename(request));
    }

    public AuthResponse delete(int userId, int folderId) {
        folderRequestValidator.validateDelete(userId, folderId);
        deleteFolderUseCase.execute(userId, folderId);
        return new AuthResponse(true, "Folder successfully deleted.");
    }

    public Object deleteSafe(int userId, int folderId) {
        return apiHandler.execute(() -> delete(userId, folderId));
    }
}