package org.service.passwordman.desktopApi.mapper;

import org.service.passwordman.desktopApi.response.FolderResponse;
import org.service.passwordman.domain.model.Folder;

public class FolderDesktopMapper {

    public FolderResponse toResponse(Folder folder) {
        return new FolderResponse(
                folder.getId(),
                folder.getUserId(),
                folder.getName()
        );
    }
}