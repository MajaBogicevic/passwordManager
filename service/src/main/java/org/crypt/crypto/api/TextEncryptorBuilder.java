package org.crypt.crypto.api;

import org.crypt.crypto.cryp.AesGcmCryptoGen;
import org.crypt.crypto.cryp.CryptoGen;
import org.crypt.crypto.cryp.IvGen;
import org.crypt.crypto.cryp.RandomIvGen;
import org.crypt.crypto.keys.DataKeyProviderInterface;
import org.crypt.crypto.payload.PayloadInterface;
import org.crypt.crypto.payload.V1Payload;
import org.crypt.crypto.service.DefaultTextEncryptor;

import java.util.Objects;

public class TextEncryptorBuilder {
    private CryptoGen cryptoEngine = new AesGcmCryptoGen();
    private PayloadInterface payloadCodec = new V1Payload();
    private IvGen ivGenerator = new RandomIvGen(12);
    private DataKeyProviderInterface dataKeyProvider;

    TextEncryptorBuilder() {
    }

    public TextEncryptorBuilder withCryptoEngine(CryptoGen cryptoEngine) {
        this.cryptoEngine = Objects.requireNonNull(cryptoEngine, "cryptoEngine must not be null");
        return this;
    }

    public TextEncryptorBuilder withPayloadCodec(PayloadInterface payloadCodec) {
        this.payloadCodec = Objects.requireNonNull(payloadCodec, "payloadCodec must not be null");
        return this;
    }

    public TextEncryptorBuilder withIvGenerator(IvGen ivGenerator) {
        this.ivGenerator = Objects.requireNonNull(ivGenerator, "ivGenerator must not be null");
        return this;
    }

    public TextEncryptorBuilder withDataKeyProvider(DataKeyProviderInterface dataKeyProvider) {
        this.dataKeyProvider = Objects.requireNonNull(dataKeyProvider, "dataKeyProvider must not be null");
        return this;
    }

    public TextEncryptor build() {
        if (dataKeyProvider == null) {
            throw new IllegalStateException("dataKeyProvider must be configured");
        }
        return new DefaultTextEncryptor(cryptoEngine, payloadCodec, ivGenerator, dataKeyProvider);
    }
}
