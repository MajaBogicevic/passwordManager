package org.service.passwordman.desktopApi.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.service.passwordman.application.usecase.entry.CreatePasswordEntryUseCase;
import org.service.passwordman.application.usecase.entry.DeletePasswordEntryUseCase;
import org.service.passwordman.application.usecase.entry.GetEntriesByFolderUseCase;
import org.service.passwordman.application.usecase.entry.GetEntriesByUserUseCase;
import org.service.passwordman.application.usecase.entry.GetPasswordEntryUseCase;
import org.service.passwordman.application.usecase.entry.RevealPasswordUseCase;
import org.service.passwordman.application.usecase.entry.SearchPasswordEntriesUseCase;
import org.service.passwordman.application.usecase.entry.UpdatePasswordEntryUseCase;
import org.service.passwordman.desktopApi.mapper.PasswordEntryDesktopMapper;
import org.service.passwordman.desktopApi.request.CreatePasswordEntryRequest;
import org.service.passwordman.desktopApi.request.SearchPasswordEntriesRequest;
import org.service.passwordman.desktopApi.request.UpdatePasswordEntryRequest;
import org.service.passwordman.desktopApi.response.AuthResponse;
import org.service.passwordman.desktopApi.response.PasswordEntryResponse;
import org.service.passwordman.desktopApi.response.PasswordEntrySearchResponse;
import org.service.passwordman.desktopApi.response.RevealPasswordResponse;
import org.service.passwordman.desktopApi.validation.PasswordEntryRequestValidator;
import org.service.passwordman.domain.model.PasswordEntry;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;

public class PasswordEntryHandler {

    private final CreatePasswordEntryUseCase createPasswordEntryUseCase;
    private final GetPasswordEntryUseCase getPasswordEntryUseCase;
    private final GetEntriesByUserUseCase getEntriesByUserUseCase;
    private final GetEntriesByFolderUseCase getEntriesByFolderUseCase;
    private final RevealPasswordUseCase revealPasswordUseCase;
    private final UpdatePasswordEntryUseCase updatePasswordEntryUseCase;
    private final DeletePasswordEntryUseCase deletePasswordEntryUseCase;
    private final SearchPasswordEntriesUseCase searchPasswordEntriesUseCase;
    private final PasswordEntryDesktopMapper passwordEntryDesktopMapper;
    private final PasswordEntryRequestValidator passwordEntryRequestValidator;
    private final ApiHandler apiHandler;
    private final CurrentUserProvider currentUserProvider;

    public PasswordEntryHandler(
            CreatePasswordEntryUseCase createPasswordEntryUseCase,
            GetPasswordEntryUseCase getPasswordEntryUseCase,
            GetEntriesByUserUseCase getEntriesByUserUseCase,
            GetEntriesByFolderUseCase getEntriesByFolderUseCase,
            RevealPasswordUseCase revealPasswordUseCase,
            UpdatePasswordEntryUseCase updatePasswordEntryUseCase,
            DeletePasswordEntryUseCase deletePasswordEntryUseCase,
            PasswordEntryDesktopMapper passwordEntryDesktopMapper,
            PasswordEntryRequestValidator passwordEntryRequestValidator,
            SearchPasswordEntriesUseCase searchPasswordEntriesUseCase,
            ApiHandler apiHandler,
            CurrentUserProvider currentUserProvider
    ) {
        this.createPasswordEntryUseCase = createPasswordEntryUseCase;
        this.getPasswordEntryUseCase = getPasswordEntryUseCase;
        this.getEntriesByUserUseCase = getEntriesByUserUseCase;
        this.getEntriesByFolderUseCase = getEntriesByFolderUseCase;
        this.revealPasswordUseCase = revealPasswordUseCase;
        this.updatePasswordEntryUseCase = updatePasswordEntryUseCase;
        this.deletePasswordEntryUseCase = deletePasswordEntryUseCase;
        this.passwordEntryDesktopMapper = passwordEntryDesktopMapper;
        this.passwordEntryRequestValidator = passwordEntryRequestValidator;
        this.searchPasswordEntriesUseCase = searchPasswordEntriesUseCase;
        this.apiHandler = apiHandler;
        this.currentUserProvider = currentUserProvider;
    }

    public AuthResponse create(CreatePasswordEntryRequest request, String clientIp) {
        passwordEntryRequestValidator.validateCreate(request);

        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        createPasswordEntryUseCase.execute(
                currentUserId,
                request.getTitle(),
                request.getUrl(),
                request.getUsername(),
                request.getPlainPassword(),
                request.getNotes(),
                request.getFolderId(),
                clientIp,
                jwtTokenId
        );

        return new AuthResponse(true, "Password entry successfully created.");
    }

    public Object createSafe(CreatePasswordEntryRequest request, String clientIp) {
        return apiHandler.execute(() -> create(request, clientIp));
    }

    public PasswordEntryResponse get(int entryId) {
        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        passwordEntryRequestValidator.validateGet(entryId);

        PasswordEntry entry = getPasswordEntryUseCase.execute(currentUserId, entryId, jwtTokenId);
        return passwordEntryDesktopMapper.toResponse(entry);
    }

    public Object getSafe(int entryId) {
        return apiHandler.execute(() -> get(entryId));
    }

    public List<PasswordEntryResponse> getByCurrentUser() {
        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        return getEntriesByUserUseCase.execute(currentUserId, jwtTokenId)
                .stream()
                .map(passwordEntryDesktopMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Object getByCurrentUserSafe() {
        return apiHandler.execute(this::getByCurrentUser);
    }

    public List<PasswordEntryResponse> getByFolder(int folderId) {
        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        passwordEntryRequestValidator.validateGetByFolder(folderId);

        return getEntriesByFolderUseCase.execute(currentUserId, folderId, jwtTokenId)
                .stream()
                .map(passwordEntryDesktopMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Object getByFolderSafe(int folderId) {
        return apiHandler.execute(() -> getByFolder(folderId));
    }

    public RevealPasswordResponse revealPassword(int entryId, String ipAddress) {
        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        passwordEntryRequestValidator.validateReveal(entryId);

        String password = revealPasswordUseCase.execute(currentUserId, entryId, ipAddress, jwtTokenId);
        return new RevealPasswordResponse(entryId, password);
    }

    public Object revealPasswordSafe(int entryId, String ipAddress) {
        return apiHandler.execute(() -> revealPassword(entryId, ipAddress));
    }

    public AuthResponse update(UpdatePasswordEntryRequest request, String clientIp) {
        passwordEntryRequestValidator.validateUpdate(request);

        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        updatePasswordEntryUseCase.execute(
                currentUserId,
                request.getEntryId(),
                request.getTitle(),
                request.getUrl(),
                request.getUsername(),
                request.getPlainPassword(),
                request.getNotes(),
                request.getFolderId(),
                jwtTokenId,
                clientIp
        );

        return new AuthResponse(true, "Password entry successfully updated.");
    }

    public AuthResponse update(UpdatePasswordEntryRequest request) {
        return update(request, null);
    }

    public Object updateSafe(UpdatePasswordEntryRequest request, String clientIp) {
        return apiHandler.execute(() -> update(request, clientIp));
    }

    public Object updateSafe(UpdatePasswordEntryRequest request) {
        return apiHandler.execute(() -> update(request));
    }

    public AuthResponse delete(int entryId, String ipAddress) {
        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        passwordEntryRequestValidator.validateDelete(entryId);
        deletePasswordEntryUseCase.execute(currentUserId, entryId, ipAddress, jwtTokenId);
        
        return new AuthResponse(true, "Password entry successfully deleted.");
    }

    public Object deleteSafe(int entryId, String ipAddress) {
        return apiHandler.execute(() -> delete(entryId, ipAddress));
    }

    public PasswordEntrySearchResponse search(SearchPasswordEntriesRequest request) {
        passwordEntryRequestValidator.validateSearch(request);

        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        return passwordEntryDesktopMapper.toSearchResponse(
            searchPasswordEntriesUseCase.execute(currentUserId,request.getTitleQuery(), jwtTokenId)
        );
    }

    public Object searchSafe(SearchPasswordEntriesRequest request) {
        return apiHandler.execute(() -> search(request));
    }
}