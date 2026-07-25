package org.service.passwordman.application.port;

public interface EncryptionService {

    String encrypt(byte[] dataEncryptionKey, String plaintext);

    String decrypt(byte[] dataEncryptionKey, String encryptedData);
}