package org.service.passwordman.infrastructure.crypt;

import org.crypt.crypto.api.TextEncryptor;
import org.crypt.crypto.api.TextEncryptors;
import org.crypt.crypto.util.Base64Url;
import org.service.passwordman.application.port.EncryptionService;

public class CryptPasswordEncryptionAdapter implements EncryptionService {

    private static final String KEY_REF = "vault-dek";

    @Override
    public String encrypt(byte[] dataEncryptionKey, String plaintext) {
        TextEncryptor encryptor = buildEncryptor(dataEncryptionKey);
        return encryptor.encrypt(plaintext);
    }

    @Override
    public String decrypt(byte[] dataEncryptionKey, String encryptedData) {
        TextEncryptor encryptor = buildEncryptor(dataEncryptionKey);
        return encryptor.decrypt(encryptedData);
    }

    private TextEncryptor buildEncryptor(byte[] dataEncryptionKey) {
        String base64Key = Base64Url.encode(dataEncryptionKey);
        return TextEncryptors.localAesGcm(KEY_REF, base64Key);
    }
}