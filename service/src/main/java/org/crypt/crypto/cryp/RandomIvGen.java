package org.crypt.crypto.cryp;

import java.security.SecureRandom;

public class RandomIvGen implements IvGen{
    private final SecureRandom secureRandom = new SecureRandom();
    private final int ivLengthBytes;

    public RandomIvGen(int ivLengthBytes) {
        if (ivLengthBytes <= 0) {
            throw new IllegalArgumentException("ivLengthBytes must be greater than 0");
        }
        this.ivLengthBytes = ivLengthBytes;
    }

    @Override
    public byte[] generateIv() {
        byte[] iv = new byte[ivLengthBytes];
        secureRandom.nextBytes(iv);
        return iv;
    }
}
