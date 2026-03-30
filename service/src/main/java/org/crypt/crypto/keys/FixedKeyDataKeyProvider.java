package org.crypt.crypto.keys;

import java.util.Objects;

public final class FixedKeyDataKeyProvider implements DataKeyProviderInterface {

    private final DataKeyProviderInterface delegate;
    private final String keyRef;

    public FixedKeyDataKeyProvider(DataKeyProviderInterface delegate, String keyRef) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.keyRef = Objects.requireNonNull(keyRef, "keyRef must not be null");

        if (keyRef.isBlank()) {
            throw new IllegalArgumentException("keyRef must not be blank");
        }
        if (keyRef.contains(".")) {
            throw new IllegalArgumentException("keyRef must not contain '.' because it is used in the payload format");
        }
    }

    @Override
    public String defaultKeyRef() {
        return keyRef;
    }

    @Override
    public DataKeyMaterial generateDataKey(KeyRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return delegate.generateDataKey(new KeyRequest(keyRef, request.getContext()));
    }

    @Override
    public byte[] unwrapDataKey(EncryptedDataKey encryptedDataKey, KeyRequest request) {
        Objects.requireNonNull(encryptedDataKey, "encryptedDataKey must not be null");
        Objects.requireNonNull(request, "request must not be null");
        return delegate.unwrapDataKey(encryptedDataKey, new KeyRequest(keyRef, request.getContext()));
    }
}