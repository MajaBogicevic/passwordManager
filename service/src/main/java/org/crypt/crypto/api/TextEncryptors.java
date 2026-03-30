package org.crypt.crypto.api;

import org.crypt.crypto.keys.FixedKeyDataKeyProvider;
import org.crypt.crypto.keys.LocalAesGcmDataKey;

public class TextEncryptors {

    private TextEncryptors() {}

    public static TextEncryptorBuilder builder() {
        return new TextEncryptorBuilder();
    }

    public static TextEncryptor localAesGcm(String keyRef, String base64MasterKey) {
        return builder()
                .withDataKeyProvider(new LocalAesGcmDataKey(keyRef, base64MasterKey))
                .build();
    }

}