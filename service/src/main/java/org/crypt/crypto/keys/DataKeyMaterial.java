package org.crypt.crypto.keys;

import org.crypt.crypto.util.ZeroUtils;

import java.util.Arrays;
import java.util.Objects;

public final class DataKeyMaterial implements AutoCloseable{
    private byte[] plaintextKey;
    private final EncryptedDataKey encryptedDataKey;

    public DataKeyMaterial(byte[] plaintextKey, EncryptedDataKey encryptedDataKey) {
        Objects.requireNonNull(plaintextKey, "plaintextKey must not be null");
        this.plaintextKey = Arrays.copyOf(plaintextKey, plaintextKey.length);
        this.encryptedDataKey = Objects.requireNonNull(encryptedDataKey, "encryptedDataKey must not be null");
    }

    public byte[] plaintextKey() {
        return Arrays.copyOf(plaintextKey, plaintextKey.length);
    }

    public EncryptedDataKey encryptedDataKey() {
        return encryptedDataKey;
    }

    @Override
    public void close() {
        ZeroUtils.zero(plaintextKey);
        plaintextKey = new byte[0];
    }
}
