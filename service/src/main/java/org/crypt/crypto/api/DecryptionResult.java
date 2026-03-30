package org.crypt.crypto.api;

import java.util.Objects;

public class DecryptionResult {
    private final String plainText;
    private final String version;
    private final String algorithm;
    private final String keyRef;

    public DecryptionResult(String plainText, String version, String algorithm, String keyRef) {
        this.plainText = Objects.requireNonNull(plainText, "plainText must not be null");
        this.version = Objects.requireNonNull(version, "version must not be null");
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm must not be null");
        this.keyRef = Objects.requireNonNull(keyRef, "keyRef must not be null");
    }

    public String getPlainText() {
        return plainText;
    }

    public String getVersion() {
        return version;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getKeyRef() {
        return keyRef;
    }
}