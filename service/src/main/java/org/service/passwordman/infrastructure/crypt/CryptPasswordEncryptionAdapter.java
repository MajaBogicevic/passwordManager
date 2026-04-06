package org.service.passwordman.infrastructure.crypt;

import org.crypt.crypto.api.TextEncryptor;
import org.crypt.crypto.api.TextEncryptors;
import org.service.passwordman.application.port.EncryptionService;

public class CryptPasswordEncryptionAdapter implements EncryptionService {

    private final TextEncryptor textEncryptor;

    public CryptPasswordEncryptionAdapter(String keyRef, String base64MasterKey) {
        this.textEncryptor = TextEncryptors.localAesGcm(keyRef, base64MasterKey);
    }

    @Override
    public String encrypt(String data) {
        return textEncryptor.encrypt(data);
    }

    @Override
    public String decrypt(String encryptedData) {
        return textEncryptor.decrypt(encryptedData);
    }
}