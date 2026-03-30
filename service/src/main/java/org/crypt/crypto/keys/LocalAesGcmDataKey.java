package org.crypt.crypto.keys;

import org.crypt.crypto.cryp.AesGcmCryptoGen;
import org.crypt.crypto.cryp.CipherResult;
import org.crypt.crypto.cryp.CryptoGen;
import org.crypt.crypto.cryp.IvGen;
import org.crypt.crypto.cryp.RandomIvGen;
import org.crypt.crypto.exception.PayloadParseException;
import org.crypt.crypto.exception.KeyManagementException;
import org.crypt.crypto.util.Base64Url;
import org.crypt.crypto.util.Utf8;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

public class LocalAesGcmDataKey implements DataKeyProviderInterface{
    private static final String PROVIDER = "LOCAL-AES";
    private static final String EDK_PREFIX = "L1";
    private static final int DEK_BYTES = 32;

    private final String keyRef;
    private final byte[] masterKey;
    private final CryptoGen cryptoEngine;
    private final IvGen wrapIvGenerator;
    private final SecureRandom secureRandom = new SecureRandom();

    public LocalAesGcmDataKey(String keyRef, String base64MasterKey) {
        this(keyRef, Base64Url.decode(base64MasterKey), new AesGcmCryptoGen(), new RandomIvGen(12));
    }

    public LocalAesGcmDataKey(String keyRef,
                                              byte[] masterKey,
                                              CryptoGen cryptoEngine,
                                              IvGen wrapIvGenerator) {
        this.keyRef = validateKeyRef(keyRef);
        this.masterKey = Arrays.copyOf(Objects.requireNonNull(masterKey, "masterKey must not be null"), masterKey.length);
        if (this.masterKey.length != 32) {
            throw new IllegalArgumentException("masterKey must be 32 bytes (AES-256)");
        }
        this.cryptoEngine = Objects.requireNonNull(cryptoEngine, "cryptoEngine must not be null");
        this.wrapIvGenerator = Objects.requireNonNull(wrapIvGenerator, "wrapIvGenerator must not be null");
    }

    @Override
    public String defaultKeyRef() {
        return keyRef;
    }

    @Override
    public DataKeyMaterial generateDataKey(KeyRequest request) {
        ensureSupportedKeyRef(request.getKeyRef());
        byte[] dek = new byte[DEK_BYTES];
        secureRandom.nextBytes(dek);

        try {
            byte[] wrapIv = wrapIvGenerator.generateIv();
            byte[] wrapAad = internalWrapAad(request.getKeyRef());
            CipherResult wrapped = cryptoEngine.encrypt(dek, masterKey, wrapIv, wrapAad);
            String serialized = EDK_PREFIX + "~" +
                    Base64Url.encode(wrapIv) + "~" +
                    Base64Url.encode(wrapped.getCiphertextWithTag());

            EncryptedDataKey encryptedDataKey = new EncryptedDataKey(serialized, new KeyDescriptor(keyRef, PROVIDER));
            return new DataKeyMaterial(dek, encryptedDataKey);
        } catch (RuntimeException e) {
            Arrays.fill(dek, (byte) 0);
            throw new KeyManagementException("Failed to generate or wrap data key", e);
        }
    }

    @Override
    public byte[] unwrapDataKey(EncryptedDataKey encryptedDataKey, KeyRequest request) {
        Objects.requireNonNull(encryptedDataKey, "encryptedDataKey must not be null");
        Objects.requireNonNull(request, "request must not be null");
        ensureSupportedKeyRef(request.getKeyRef());

        String[] parts = encryptedDataKey.getSerializedForm().split("~", -1);
        if (parts.length != 3 || !EDK_PREFIX.equals(parts[0])) {
            throw new PayloadParseException("Unsupported local encrypted data key format");
        }

        final byte[] wrapIv;
        final byte[] wrappedDek;

        try {
            wrapIv = Base64Url.decode(parts[1]);
            wrappedDek = Base64Url.decode(parts[2]);
        } catch (IllegalArgumentException e) {
            throw new PayloadParseException("Local encrypted data key contains invalid Base64 content", e);
        }

        try {
            return cryptoEngine.decrypt(wrappedDek, masterKey, wrapIv, internalWrapAad(request.getKeyRef()));
        } catch (RuntimeException e) {
            throw new KeyManagementException("Failed to unwrap local data key", e);
        }
    }

    private byte[] internalWrapAad(String currentKeyRef) {
        return Utf8.bytes("wrap:" + currentKeyRef);
    }

    private void ensureSupportedKeyRef(String requestKeyRef) {
        String normalized = validateKeyRef(requestKeyRef);
        if (!keyRef.equals(normalized)) {
            throw new KeyManagementException("Local provider is configured for keyRef '" + keyRef +
                    "' but received '" + normalized + "'");
        }
    }

    private static String validateKeyRef(String keyRef) {
        Objects.requireNonNull(keyRef, "keyRef must not be null");
        if (keyRef.isBlank()) {
            throw new IllegalArgumentException("keyRef must not be blank");
        }
        if (keyRef.contains(".")) {
            throw new IllegalArgumentException("keyRef must not contain '.' because it is used in the payload format");
        }
        return keyRef;
    }
}
