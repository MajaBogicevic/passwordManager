package org.service.passwordman.desktopApi.handler;

import java.util.List;
import java.util.stream.Collectors;

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
import org.service.passwordman.infrastructure.security.CurrentUserProvider;

public class FolderHandler {

    private final CreateFolderUseCase createFolderUseCase;
    private final GetFolderUseCase getFolderUseCase;
    private final GetFoldersByUserUseCase getFoldersByUserUseCase;
    private final RenameFolderUseCase renameFolderUseCase;
    private final DeleteFolderUseCase deleteFolderUseCase;
    private final FolderDesktopMapper folderDesktopMapper;
    private final FolderRequestValidator folderRequestValidator;
    private final ApiHandler apiHandler;
    private final CurrentUserProvider currentUserProvider;

    public FolderHandler(
            CreateFolderUseCase createFolderUseCase,
            GetFolderUseCase getFolderUseCase,
            GetFoldersByUserUseCase getFoldersByUserUseCase,
            RenameFolderUseCase renameFolderUseCase,
            DeleteFolderUseCase deleteFolderUseCase,
            FolderDesktopMapper folderDesktopMapper,
            FolderRequestValidator folderRequestValidator,
            ApiHandler apiHandler,
            CurrentUserProvider currentUserProvider
    ) {
        this.createFolderUseCase = createFolderUseCase;
        this.getFolderUseCase = getFolderUseCase;
        this.getFoldersByUserUseCase = getFoldersByUserUseCase;
        this.renameFolderUseCase = renameFolderUseCase;
        this.deleteFolderUseCase = deleteFolderUseCase;
        this.folderDesktopMapper = folderDesktopMapper;
        this.folderRequestValidator = folderRequestValidator;
        this.apiHandler = apiHandler;
        this.currentUserProvider = currentUserProvider;
    }

    public AuthResponse create(CreateFolderRequest request, String clientIp) {
        folderRequestValidator.validateCreate(request);
        int currentUserId = currentUserProvider.requireUserId();
        createFolderUseCase.execute(currentUserId, request.getFolderName(), clientIp);
        return new AuthResponse(true, "Folder successfully created.");
    }

    public AuthResponse create(CreateFolderRequest request) {
        return create(request, null);
    }

    public Object createSafe(CreateFolderRequest request, String clientIp) {
        return apiHandler.execute(() -> create(request, clientIp));
    }

    public Object createSafe(CreateFolderRequest request) {
        return apiHandler.execute(() -> create(request));
    }

    public FolderResponse get(int folderId) {
        int currentUserId = currentUserProvider.requireUserId();
        Folder folder = getFolderUseCase.execute(currentUserId, folderId);
        return folderDesktopMapper.toResponse(folder);
    }

    public Object getSafe(int folderId) {
        return apiHandler.execute(() -> get(folderId));
    }

    public List<FolderResponse> getByUserCurrentUser() {
        int currentUserId = currentUserProvider.requireUserId();
        return getFoldersByUserUseCase.execute(currentUserId)
                .stream()
                .map(folderDesktopMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Object getByUserSafe() {
        return apiHandler.execute(this::getByUserCurrentUser);
    }

    public AuthResponse rename(RenameFolderRequest request, String clientIp) {
        folderRequestValidator.validateRename(request);

        int currentUserId = currentUserProvider.requireUserId();

        renameFolderUseCase.execute(
                currentUserId,
                request.getFolderId(),
                request.getNewName(),
                clientIp
        );

        return new AuthResponse(true, "Folder successfully renamed.");
    }

    public AuthResponse rename(RenameFolderRequest request) {
        return rename(request, null);
    }

    public Object renameSafe(RenameFolderRequest request, String clientIp) {
        return apiHandler.execute(() -> rename(request, clientIp));
    }

    public Object renameSafe(RenameFolderRequest request) {
        return apiHandler.execute(() -> rename(request));
    }

    public AuthResponse delete(int folderId, String clientIp) {
        int currentUserId = currentUserProvider.requireUserId();
        folderRequestValidator.validateDelete(folderId);
        deleteFolderUseCase.execute(currentUserId, folderId, clientIp);
        return new AuthResponse(true, "Folder successfully deleted.");
    }

    public AuthResponse delete(int folderId) {
        return delete(folderId, null);
    }

    public Object deleteSafe(int folderId, String clientIp) {
        return apiHandler.execute(() -> delete(folderId, clientIp));
    }

    public Object deleteSafe(int folderId) {
        return apiHandler.execute(() -> delete(folderId));
    }
}