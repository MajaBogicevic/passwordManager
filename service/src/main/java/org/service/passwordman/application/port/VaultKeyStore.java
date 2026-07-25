package org.service.passwordman.application.port;

import java.util.Optional;

public interface VaultKeyStore {

    void store(int userId, String jwtTokenId, byte[] dataEncryptionKey);

    Optional<byte[]> get(int userId, String jwtTokenId);

    void clear(int userId, String jwtTokenId);

    void clearAllForUser(int userId);
}