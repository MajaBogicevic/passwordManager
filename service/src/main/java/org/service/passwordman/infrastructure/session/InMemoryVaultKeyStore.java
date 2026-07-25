package org.service.passwordman.infrastructure.session;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.crypt.crypto.util.ZeroUtils;
import org.service.passwordman.application.port.VaultKeyStore;

public class InMemoryVaultKeyStore implements VaultKeyStore {

    private final Map<String, byte[]> keysBySessionKey = new ConcurrentHashMap<>();

    @Override
    public void store(int userId, String jwtTokenId, byte[] dataEncryptionKey) {
        keysBySessionKey.put(buildKey(userId, jwtTokenId), Arrays.copyOf(dataEncryptionKey, dataEncryptionKey.length));
    }

    @Override
    public Optional<byte[]> get(int userId, String jwtTokenId) {
        byte[] key = keysBySessionKey.get(buildKey(userId, jwtTokenId));
        if (key == null) {
            return Optional.empty();
        }
        return Optional.of(Arrays.copyOf(key, key.length));
    }

    @Override
    public void clear(int userId, String jwtTokenId) {
        byte[] removed = keysBySessionKey.remove(buildKey(userId, jwtTokenId));
        ZeroUtils.zero(removed);
    }

    @Override
    public void clearAllForUser(int userId) {
        String prefix = userId + ":";
        keysBySessionKey.keySet().removeIf(key -> {
            boolean matches = key.startsWith(prefix);
            if (matches) {
                ZeroUtils.zero(keysBySessionKey.get(key));
            }
            return matches;
        });
    }

    private String buildKey(int userId, String jwtTokenId) {
        return userId + ":" + jwtTokenId;
    }
}