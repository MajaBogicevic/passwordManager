package org.crypt.crypto.payload;

import org.crypt.crypto.keys.EncryptedDataKey;

import java.util.Arrays;
import java.util.Objects;

public class EncryptedPayload {

    private final PayloadVersion version;
    private final String algorithm;
    private final String keyRef;
    private final byte[] iv;
    private final EncryptedDataKey encryptedDataKey;
    private final byte[] ciphertextWithTag;

    public EncryptedPayload(PayloadVersion version,
                            String algorithm,
                            String keyRef,
                            byte[] iv,
                            EncryptedDataKey encryptedDataKey,
                            byte[] ciphertextWithTag) {

        this.version = Objects.requireNonNull(version, "version must not be null");
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm must not be null");
        this.keyRef = Objects.requireNonNull(keyRef, "keyRef must not be null");
        this.iv = Arrays.copyOf(Objects.requireNonNull(iv, "iv must not be null"), iv.length);
        this.encryptedDataKey = Objects.requireNonNull(encryptedDataKey, "encryptedDataKey must not be null");
        this.ciphertextWithTag = Arrays.copyOf(
                Objects.requireNonNull(ciphertextWithTag, "ciphertextWithTag must not be null"),
                ciphertextWithTag.length
        );
    }

    public PayloadVersion getVersion() {
        return version;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getKeyRef() {
        return keyRef;
    }

    public byte[] getIv() {
        return Arrays.copyOf(iv, iv.length);
    }

    public EncryptedDataKey getEncryptedDataKey() {
        return encryptedDataKey;
    }

    public byte[] getCiphertextWithTag() {
        return Arrays.copyOf(ciphertextWithTag, ciphertextWithTag.length);
    }
}