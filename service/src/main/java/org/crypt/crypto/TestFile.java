package org.crypt.crypto;

import org.crypt.crypto.api.TextEncryptor;
import org.crypt.crypto.api.TextEncryptors;

public class TestFile {

    public static void main(String[] args) {
        String keyRef = "local-key";
        String base64MasterKey = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

        TextEncryptor encryptor = TextEncryptors.localAesGcm(keyRef, base64MasterKey);

        String original = "neki-kriptovani-podaci-123";
        String encrypted = encryptor.encrypt(original);
        String decrypted = encryptor.decrypt(encrypted);

        System.out.println("Original:  " + original);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
        System.out.println("Uspesno:   " + original.equals(decrypted));
    }
}