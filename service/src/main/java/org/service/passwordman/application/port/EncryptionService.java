package org.service.passwordman.application.port;

public interface EncryptionService {

    String encrypt(String data);

    String decrypt(String encryptedData);
}