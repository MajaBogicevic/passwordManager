package org.service.passwordman.application.usecase.entry;

import org.service.passwordman.domain.model.PasswordEntry;

import java.util.List;

public interface GetEntriesByUserUseCase {
    List<PasswordEntry> execute(int userId, String jwtTokenId);
}