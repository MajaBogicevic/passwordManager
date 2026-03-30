package org.crypt.crypto.api;

import java.util.Map;
import java.util.Objects;
import java.util.Collections;

public class EncryptionRequest {
    private final String plainText;
    private final Map<String, String> aad;
    private final String keyRef;

    public EncryptionRequest(String plainText) {
        this(plainText, null, null);
    }

    public EncryptionRequest(String plainText, Map<String, String> aad) {
        this(plainText, aad, null);
    }

    public EncryptionRequest(String plainText, Map<String, String> aad, String keyRef) {
        this.plainText = Objects.requireNonNull(plainText, "plain text must not be null");
        this.aad = aad == null ? Collections.emptyMap() : Map.copyOf(aad);
        this.keyRef = keyRef;
    }

    public Map<String, String> getAad() {
        return aad;
    }

    public String getPlainText() {
        return plainText;
    }

    public String getKeyRef() {
        return keyRef;
    }
}