package org.service.passwordman.desktopApi.mapper;

import org.service.passwordman.desktopApi.response.PasswordEntryListItemResponse;
import org.service.passwordman.desktopApi.response.PasswordEntryResponse;
import org.service.passwordman.desktopApi.response.PasswordEntrySearchResponse;
import org.service.passwordman.domain.model.PasswordEntry;

import java.util.List;
import java.util.stream.Collectors;

public class PasswordEntryDesktopMapper {

    public PasswordEntryResponse toResponse(PasswordEntry entry) {
        return new PasswordEntryResponse(
                entry.getId(),
                entry.getUserId(),
                entry.getTitle(),
                entry.getUrl(),
                entry.getUsername(),
                entry.getNotes(),
                entry.getFolderId(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }

    public PasswordEntryListItemResponse toListItemResponse(PasswordEntry entry) {
        return new PasswordEntryListItemResponse(
                entry.getId(),
                entry.getUserId(),
                entry.getTitle(),
                entry.getUrl(),
                entry.getUsername(),
                entry.getNotes(),
                entry.getFolderId(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }

    public PasswordEntrySearchResponse toSearchResponse(List<PasswordEntry> entries) {
        List<PasswordEntryListItemResponse> items = entries.stream()
                .map(this::toListItemResponse)
                .collect(Collectors.toList());

        return new PasswordEntrySearchResponse(items);
    }
}