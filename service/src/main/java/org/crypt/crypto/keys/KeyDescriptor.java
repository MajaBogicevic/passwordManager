package org.crypt.crypto.keys;

import java.util.Objects;

public class KeyDescriptor {
    private final String keyRef;
    private final String provider;

    public KeyDescriptor(String keyRef, String provider) {
        this.keyRef = Objects.requireNonNull(keyRef, "keyRef must not be null");
        this.provider = Objects.requireNonNull(provider, "provider must not be null");
    }

    public String getKeyRef() {
        return keyRef;
    }

    public String getProvider() {
        return provider;
    }
}
