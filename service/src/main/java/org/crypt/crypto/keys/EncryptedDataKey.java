package org.crypt.crypto.keys;

import java.util.Objects;

public class EncryptedDataKey {
    private final String serializedForm;
    private final KeyDescriptor keyDescriptor;

    public EncryptedDataKey(String serializedForm, KeyDescriptor keyDescriptor) {
        this.serializedForm = Objects.requireNonNull(serializedForm, "serializedForm must not be null");
        this.keyDescriptor = Objects.requireNonNull(keyDescriptor, "keyDescriptor must not be null");

        if (this.serializedForm.isBlank()) {
            throw new IllegalArgumentException("serializedForm must not be blank");
        }
    }

    public String getSerializedForm() {
        return serializedForm;
    }

    public KeyDescriptor getKeyDescriptor() {
        return keyDescriptor;
    }
}
