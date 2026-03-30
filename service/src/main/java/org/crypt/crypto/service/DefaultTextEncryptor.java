package org.crypt.crypto.service;

import org.crypt.crypto.api.DecryptionRequest;
import org.crypt.crypto.api.DecryptionResult;
import org.crypt.crypto.api.EncryptionRequest;
import org.crypt.crypto.api.EncryptionResult;
import org.crypt.crypto.api.TextEncryptor;
import org.crypt.crypto.cryp.CipherResult;
import org.crypt.crypto.cryp.CryptoGen;
import org.crypt.crypto.cryp.IvGen;
import org.crypt.crypto.keys.DataKeyMaterial;
import org.crypt.crypto.keys.DataKeyProviderInterface;
import org.crypt.crypto.keys.EncryptedDataKey;
import org.crypt.crypto.keys.KeyRequest;
import org.crypt.crypto.payload.EncryptedPayload;
import org.crypt.crypto.payload.PayloadInterface;
import org.crypt.crypto.payload.PayloadVersion;
import org.crypt.crypto.payload.V1Payload;
import org.crypt.crypto.util.AadCode;
import org.crypt.crypto.util.Utf8;
import org.crypt.crypto.util.ZeroUtils;

import java.util.Objects;

public class DefaultTextEncryptor implements TextEncryptor{
    private final CryptoGen cryptoEngine;
    private final PayloadInterface payloadCodec;
    private final IvGen ivGenerator;
    private final DataKeyProviderInterface dataKeyProvider;

    public DefaultTextEncryptor(CryptoGen cryptoEngine,
                                PayloadInterface payloadCodec,
                                IvGen ivGenerator,
                                DataKeyProviderInterface dataKeyProvider) {
        this.cryptoEngine = Objects.requireNonNull(cryptoEngine, "cryptoEngine must not be null");
        this.payloadCodec = Objects.requireNonNull(payloadCodec, "payloadCodec must not be null");
        this.ivGenerator = Objects.requireNonNull(ivGenerator, "ivGenerator must not be null");
        this.dataKeyProvider = Objects.requireNonNull(dataKeyProvider, "dataKeyProvider must not be null");
    }

    @Override
    public String encrypt(String plaintext) {
        return encrypt(new EncryptionRequest(plaintext)).getPayload();
    }

    @Override
    public String decrypt(String payload) {
        return decrypt(new DecryptionRequest(payload, null)).getPlainText();
    }

    @Override
    public EncryptionResult encrypt(EncryptionRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        byte[] plaintextBytes = Utf8.bytes(request.getPlainText());
        byte[] aadBytes = AadCode.encode(request.getAad());
        byte[] dataKey = null;

        try {
            String keyRef = resolveKeyRef(request);
            try (DataKeyMaterial keyMaterial = dataKeyProvider.generateDataKey(new KeyRequest(keyRef, request.getAad()))) {

                byte[] iv = ivGenerator.generateIv();
                dataKey = keyMaterial.plaintextKey();

                CipherResult cipherResult = cryptoEngine.encrypt(plaintextBytes, dataKey, iv, aadBytes);

                EncryptedPayload payload = new EncryptedPayload(
                        PayloadVersion.V1,
                        V1Payload.ALGORITHM,
                        keyRef,
                        iv,
                        keyMaterial.encryptedDataKey(),
                        cipherResult.getCiphertextWithTag()
                );

                return new EncryptionResult(
                        payloadCodec.encode(payload),
                        PayloadVersion.V1.token(),
                        V1Payload.ALGORITHM,
                        keyRef
                );
            }
        } finally {
            ZeroUtils.zero(dataKey);
            ZeroUtils.zero(plaintextBytes);
            ZeroUtils.zero(aadBytes);
        }
    }

    @Override
    public DecryptionResult decrypt(DecryptionRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        byte[] aadBytes = AadCode.encode(request.aad());
        byte[] dataKey = null;

        try {
            EncryptedPayload payload = payloadCodec.decode(request.payload());

            dataKey = dataKeyProvider.unwrapDataKey(
                    payload.getEncryptedDataKey(),
                    new KeyRequest(payload.getKeyRef(), request.aad())
            );

            byte[] plaintextBytes = cryptoEngine.decrypt(
                    payload.getCiphertextWithTag(),
                    dataKey,
                    payload.getIv(),
                    aadBytes
            );

            try {
                String plaintext = Utf8.string(plaintextBytes);
                return new DecryptionResult(
                        plaintext,
                        payload.getVersion().token(),
                        payload.getAlgorithm(),
                        payload.getKeyRef()
                );
            } finally {
                ZeroUtils.zero(plaintextBytes);
            }
        } finally {
            ZeroUtils.zero(dataKey);
            ZeroUtils.zero(aadBytes);
        }
    }

    private String resolveKeyRef(EncryptionRequest request) {
        return dataKeyProvider.defaultKeyRef();
    }
}
