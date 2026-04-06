package org.service.passwordman.desktopApi.handler;

import org.service.passwordman.application.usecase.entry.CopyPasswordUseCase;
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
import org.service.passwordman.desktopApi.response.CopyPasswordResponse;
import org.service.passwordman.desktopApi.response.PasswordEntryResponse;
import org.service.passwordman.desktopApi.response.PasswordEntrySearchResponse;
import org.service.passwordman.desktopApi.response.RevealPasswordResponse;
import org.service.passwordman.desktopApi.validation.PasswordEntryRequestValidator;
import org.service.passwordman.domain.model.PasswordEntry;

import java.util.List;
import java.util.stream.Collectors;

public class PasswordEntryHandler {

    private final CreatePasswordEntryUseCase createPasswordEntryUseCase;
    private final GetPasswordEntryUseCase getPasswordEntryUseCase;
    private final GetEntriesByUserUseCase getEntriesByUserUseCase;
    private final GetEntriesByFolderUseCase getEntriesByFolderUseCase;
    private final RevealPasswordUseCase revealPasswordUseCase;
    private final CopyPasswordUseCase copyPasswordUseCase;
    private final UpdatePasswordEntryUseCase updatePasswordEntryUseCase;
    private final DeletePasswordEntryUseCase deletePasswordEntryUseCase;
    private final SearchPasswordEntriesUseCase searchPasswordEntriesUseCase;
    private final PasswordEntryDesktopMapper passwordEntryDesktopMapper;
    private final PasswordEntryRequestValidator passwordEntryRequestValidator;
    private final ApiHandler apiHandler;

    public PasswordEntryHandler(
            CreatePasswordEntryUseCase createPasswordEntryUseCase,
            GetPasswordEntryUseCase getPasswordEntryUseCase,
            GetEntriesByUserUseCase getEntriesByUserUseCase,
            GetEntriesByFolderUseCase getEntriesByFolderUseCase,
            RevealPasswordUseCase revealPasswordUseCase,
            CopyPasswordUseCase copyPasswordUseCase,
            UpdatePasswordEntryUseCase updatePasswordEntryUseCase,
            DeletePasswordEntryUseCase deletePasswordEntryUseCase,
            PasswordEntryDesktopMapper passwordEntryDesktopMapper,
            PasswordEntryRequestValidator passwordEntryRequestValidator,
            SearchPasswordEntriesUseCase searchPasswordEntriesUseCase,
            ApiHandler apiHandler
    ) {
        this.createPasswordEntryUseCase = createPasswordEntryUseCase;
        this.getPasswordEntryUseCase = getPasswordEntryUseCase;
        this.getEntriesByUserUseCase = getEntriesByUserUseCase;
        this.getEntriesByFolderUseCase = getEntriesByFolderUseCase;
        this.revealPasswordUseCase = revealPasswordUseCase;
        this.copyPasswordUseCase = copyPasswordUseCase;
        this.updatePasswordEntryUseCase = updatePasswordEntryUseCase;
        this.deletePasswordEntryUseCase = deletePasswordEntryUseCase;
        this.passwordEntryDesktopMapper = passwordEntryDesktopMapper;
        this.passwordEntryRequestValidator = passwordEntryRequestValidator;
        this.searchPasswordEntriesUseCase = searchPasswordEntriesUseCase;
        this.apiHandler = apiHandler;
    }

    public AuthResponse create(CreatePasswordEntryRequest request) {
        passwordEntryRequestValidator.validateCreate(request);

        createPasswordEntryUseCase.execute(
                request.getUserId(),
                request.getTitle(),
                request.getUrl(),
                request.getUsername(),
                request.getPlainPassword(),
                request.getNotes(),
                request.getFolderId()
        );

        return new AuthResponse(true, "Password entry successfully created.");
    }

    public Object createSafe(CreatePasswordEntryRequest request) {
        return apiHandler.execute(() -> create(request));
    }

    public PasswordEntryResponse get(int userId, int entryId) {
        passwordEntryRequestValidator.validateGet(userId, entryId);

        PasswordEntry entry = getPasswordEntryUseCase.execute(userId, entryId);
        return passwordEntryDesktopMapper.toResponse(entry);
    }

    public Object getSafe(int userId, int entryId) {
        return apiHandler.execute(() -> get(userId, entryId));
    }

    public List<PasswordEntryResponse> getByUser(int userId) {
        if (userId <= 0) {
            throw new org.service.passwordman.domain.exception.ValidationException("User id must be greater than 0.");
        }

        return getEntriesByUserUseCase.execute(userId)
                .stream()
                .map(passwordEntryDesktopMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Object getByUserSafe(int userId) {
        return apiHandler.execute(() -> getByUser(userId));
    }

    public List<PasswordEntryResponse> getByFolder(int userId, int folderId) {
        passwordEntryRequestValidator.validateGetByFolder(userId, folderId);

        return getEntriesByFolderUseCase.execute(userId, folderId)
                .stream()
                .map(passwordEntryDesktopMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Object getByFolderSafe(int userId, int folderId) {
        return apiHandler.execute(() -> getByFolder(userId, folderId));
    }

    public RevealPasswordResponse revealPassword(int userId, int entryId) {
        passwordEntryRequestValidator.validateReveal(userId, entryId);

        String password = revealPasswordUseCase.execute(userId, entryId);
        return new RevealPasswordResponse(entryId, password);
    }

    public Object revealPasswordSafe(int userId, int entryId) {
        return apiHandler.execute(() -> revealPassword(userId, entryId));
    }

    public CopyPasswordResponse copyPassword(int userId, int entryId) {
        passwordEntryRequestValidator.validateReveal(userId, entryId);

        String password = copyPasswordUseCase.execute(userId, entryId);
        return new CopyPasswordResponse(entryId, password, true, "Password successfully prepared for copy.");
    }

    public Object copyPasswordSafe(int userId, int entryId) {
        return apiHandler.execute(() -> copyPassword(userId, entryId));
    }

    public AuthResponse update(UpdatePasswordEntryRequest request) {
        passwordEntryRequestValidator.validateUpdate(request);

        updatePasswordEntryUseCase.execute(
                request.getUserId(),
                request.getEntryId(),
                request.getTitle(),
                request.getUrl(),
                request.getUsername(),
                request.getPlainPassword(),
                request.getNotes(),
                request.getFolderId()
        );

        return new AuthResponse(true, "Password entry successfully updated.");
    }

    public Object updateSafe(UpdatePasswordEntryRequest request) {
        return apiHandler.execute(() -> update(request));
    }

    public AuthResponse delete(int userId, int entryId) {
        passwordEntryRequestValidator.validateDelete(userId, entryId);
        deletePasswordEntryUseCase.execute(userId, entryId);
        return new AuthResponse(true, "Password entry successfully deleted.");
    }

    public Object deleteSafe(int userId, int entryId) {
        return apiHandler.execute(() -> delete(userId, entryId));
    }

    public PasswordEntrySearchResponse search(SearchPasswordEntriesRequest request) {
        passwordEntryRequestValidator.validateSearch(request);

        return passwordEntryDesktopMapper.toSearchResponse(
                searchPasswordEntriesUseCase.execute(
                        request.getUserId(),
                        request.getTitleQuery()
                )
        );
    }

    public Object searchSafe(SearchPasswordEntriesRequest request) {
        return apiHandler.execute(() -> search(request));
    }
}