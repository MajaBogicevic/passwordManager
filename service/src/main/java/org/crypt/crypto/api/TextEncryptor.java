package org.crypt.crypto.api;

public interface TextEncryptor {
    String encrypt(String plaintext);

    String decrypt(String payload);

    EncryptionResult encrypt(EncryptionRequest request);

    DecryptionResult decrypt(DecryptionRequest request);
}
