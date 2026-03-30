package org.crypt.crypto.api;

import java.util.Objects;

public class EncryptionResult {
    private final String payload;
    private final String version;
    private final String algorithm;
    private final String keyRef;

    public EncryptionResult(String payload, String version, String algorithm, String keyRef) {
        this.payload = Objects.requireNonNull(payload, "payload must not be null");
        this.version = Objects.requireNonNull(version, "version must not be null");
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm must not be null");
        this.keyRef = Objects.requireNonNull(keyRef, "keyRef must not be null");
    }

    public String getPayload() {
        return payload;
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