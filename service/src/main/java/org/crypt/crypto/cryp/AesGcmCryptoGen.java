package org.crypt.crypto.cryp;

import org.crypt.crypto.exception.AuthenticationTagException;
import org.crypt.crypto.exception.EncryptionException;
import org.crypt.crypto.exception.DecryptionException;

import javax.crypto.Cipher;
import javax.crypto.AEADBadTagException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

public class AesGcmCryptoGen implements CryptoGen {
    public static final String TRANSFORMATION = "AES/GCM/NoPadding";
    public static final int TAG_LENGTH_BITS = 128;

    @Override
    public CipherResult encrypt(byte[] plaintext, byte[] key, byte[] iv, byte[] aad) {
        Objects.requireNonNull(plaintext, "plaintext must not be null");
        validateKey(key);
        validateIv(iv);

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            if (aad != null && aad.length > 0) {
                cipher.updateAAD(aad);
            }
            return new CipherResult(cipher.doFinal(plaintext));
        } catch (Exception e) {
            throw new EncryptionException("AES-GCM encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] ciphertextWithTag, byte[] key, byte[] iv, byte[] aad) {
        Objects.requireNonNull(ciphertextWithTag, "ciphertextWithTag must not be null");
        validateKey(key);
        validateIv(iv);

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            if (aad != null && aad.length > 0) {
                cipher.updateAAD(aad);
            }
            return cipher.doFinal(ciphertextWithTag);
        } catch (AEADBadTagException e) {
            throw new AuthenticationTagException("AES-GCM authentication failed. The payload, key, IV, or AAD is incorrect.", e);
        } catch (Exception e) {
            throw new DecryptionException("AES-GCM decryption failed", e);
        }
    }

    private static void validateKey(byte[] key) {
        Objects.requireNonNull(key, "key must not be null");
        int length = key.length;
        if (length != 16 && length != 24 && length != 32) {
            throw new IllegalArgumentException("AES key must be 16, 24, or 32 bytes long");
        }
    }

    private static void validateIv(byte[] iv) {
        Objects.requireNonNull(iv, "iv must not be null");
        if (iv.length != 12) {
            throw new IllegalArgumentException("AES-GCM IV must be 12 bytes long");
        }
    }
}
