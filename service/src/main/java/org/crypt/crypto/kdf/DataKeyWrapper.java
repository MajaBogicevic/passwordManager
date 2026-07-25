package org.crypt.crypto.kdf;

import java.security.SecureRandom;

import org.crypt.crypto.cryp.AesGcmCryptoGen;
import org.crypt.crypto.cryp.CipherResult;
import org.crypt.crypto.exception.PayloadParseException;
import org.crypt.crypto.util.Base64Url;

public final class DataKeyWrapper {

    private static final AesGcmCryptoGen CRYPTO = new AesGcmCryptoGen();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int IV_LENGTH_BYTES = 12;

    private DataKeyWrapper() {
    }

    public static String wrap(byte[] dataEncryptionKey, byte[] keyEncryptionKey) {
        byte[] iv = new byte[IV_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(iv);

        CipherResult result = CRYPTO.encrypt(dataEncryptionKey, keyEncryptionKey, iv, null);

        return Base64Url.encode(iv) + "." + Base64Url.encode(result.getCiphertextWithTag());
    }

    public static byte[] unwrap(String wrappedDataKey, byte[] keyEncryptionKey) {
        String[] parts = wrappedDataKey.split("\\.", -1);
        if (parts.length != 2) {
            throw new PayloadParseException("wrappedDataKey must have exactly 2 dot-separated segments");
        }

        byte[] iv = Base64Url.decode(parts[0]);
        byte[] ciphertextWithTag = Base64Url.decode(parts[1]);

        return CRYPTO.decrypt(ciphertextWithTag, keyEncryptionKey, iv, null);
    }
}