package org.service.passwordman.application.service.entry;

import java.util.List;
import java.util.stream.Collectors;

import org.service.passwordman.application.port.EncryptionService;
import org.service.passwordman.domain.model.PasswordEntry;

final class PasswordEntryDecryptor {

    private PasswordEntryDecryptor() {
    }

    static PasswordEntry decryptMetadata(
            PasswordEntry entry,
            byte[] dataEncryptionKey,
            EncryptionService encryptionService
    ) {
        String decryptedUsername = encryptionService.decrypt(dataEncryptionKey, entry.getUsername());
        String decryptedNotes = entry.getNotes() == null
                ? null
                : encryptionService.decrypt(dataEncryptionKey, entry.getNotes());

        return new PasswordEntry(
                entry.getId(),
                entry.getUserId(),
                entry.getTitle(),
                entry.getUrl(),
                decryptedUsername,
                entry.getEncryptedPassword(),
                decryptedNotes,
                entry.getFolderId(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }

    static List<PasswordEntry> decryptMetadata(
            List<PasswordEntry> entries,
            byte[] dataEncryptionKey,
            EncryptionService encryptionService
    ) {
        return entries.stream()
                .map(entry -> decryptMetadata(entry, dataEncryptionKey, encryptionService))
                .collect(Collectors.toList());
    }
}