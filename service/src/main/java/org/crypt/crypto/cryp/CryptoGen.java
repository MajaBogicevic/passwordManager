package org.crypt.crypto.cryp;

public interface CryptoGen {
    CipherResult encrypt(byte[] plaintext, byte[] key, byte[] iv, byte[] aad);

    byte[] decrypt(byte[] ciphertextWithTag, byte[] key, byte[] iv, byte[] aad);
}
