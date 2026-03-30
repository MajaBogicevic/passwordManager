package org.crypt.crypto.keys;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class KeyRequest {
    private final String keyRef;
    private final Map<String, String> context;

    public KeyRequest(String keyRef, Map<String, String> context) {
        this.keyRef = Objects.requireNonNull(keyRef, "keyRef must not be null");

        if (this.keyRef.isBlank()) {
            throw new IllegalArgumentException("keyRef must not be blank");
        }

        this.context = context == null ? Collections.emptyMap() : Map.copyOf(context);
    }

    public String getKeyRef() {
        return keyRef;
    }

    public Map<String, String> getContext() {
        return context;
    }
}
