package org.crypt.crypto.api;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class DecryptionRequest {
    private final String payload;
    private final Map<String, String> aad;

    public DecryptionRequest(String payload, Map<String, String> aad) {
        this.payload = Objects.requireNonNull(payload, "Payload must not be null");
        this.aad = aad == null ? Collections.emptyMap() : Map.copyOf(aad);
    }

    public DecryptionRequest(String payload){
        this(payload, null);
    }

    public String payload() {
        return payload;
    }

    public Map<String, String> aad() {
        return aad;
    }
}