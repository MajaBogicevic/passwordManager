package org.crypt.crypto.cryp;

import java.util.Arrays;
import java.util.Objects;

public class CipherResult {
    private final byte[] ciphertextWithTag;

    public CipherResult(byte[] ciphertextWithTag) {
        this.ciphertextWithTag = Arrays.copyOf(
                Objects.requireNonNull(ciphertextWithTag, "ciphertextWithTag must not be null"),
                ciphertextWithTag.length
        );
    }

    public byte[] getCiphertextWithTag() {
        return Arrays.copyOf(ciphertextWithTag, ciphertextWithTag.length);
    }
}
