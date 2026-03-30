package org.crypt.crypto.payload;

import org.crypt.crypto.exception.UnsupportedPayloadVersionException;
import org.crypt.crypto.exception.PayloadParseException;
import org.crypt.crypto.keys.EncryptedDataKey;
import org.crypt.crypto.keys.KeyDescriptor;
import org.crypt.crypto.util.Base64Url;

import java.util.Objects;

public final class V1Payload implements PayloadInterface {
    public static final String ALGORITHM = "A256GCM";
    private static final String SEGMENT_SEPARATOR_REGEX = "\\.";

    @Override
    public String encode(EncryptedPayload payload) {
        Objects.requireNonNull(payload, "payload must not be null");

        if (payload.getVersion() != PayloadVersion.V1) {
            throw new UnsupportedPayloadVersionException("Unsupported payload version: " + payload.getVersion());
        }
        if (!ALGORITHM.equals(payload.getAlgorithm())) {
            throw new PayloadParseException("Unsupported algorithm marker: " + payload.getAlgorithm());
        }
        validateSegment(payload.getKeyRef(), "keyRef");
        validateSegment(payload.getEncryptedDataKey().getSerializedForm(), "encryptedDataKey");

        return String.join(".",
                payload.getVersion().token(),
                payload.getAlgorithm(),
                payload.getKeyRef(),
                Base64Url.encode(payload.getIv()),
                payload.getEncryptedDataKey().getSerializedForm(),
                Base64Url.encode(payload.getCiphertextWithTag())
        );
    }

    @Override
    public EncryptedPayload decode(String serialized) {
        Objects.requireNonNull(serialized, "serialized must not be null");
        String[] parts = serialized.split(SEGMENT_SEPARATOR_REGEX, -1);
        if (parts.length != 6) {
            throw new PayloadParseException("Payload must have 6 dot-separated segments");
        }

        PayloadVersion version = PayloadVersion.fromToken(parts[0]);
        if (version == null) {
            throw new UnsupportedPayloadVersionException("Unsupported payload version: " + parts[0]);
        }
        if (version != PayloadVersion.V1) {
            throw new UnsupportedPayloadVersionException("Unsupported payload version: " + version.token());
        }
        if (!ALGORITHM.equals(parts[1])) {
            throw new PayloadParseException("Unsupported payload algorithm: " + parts[1]);
        }


        String keyRef = parts[2];
        validateSegment(keyRef, "keyRef");

        byte[] iv;
        byte[] ciphertext;

        try {
            iv = Base64Url.decode(parts[3]);
        } catch (IllegalArgumentException e) {
            throw new PayloadParseException("Payload IV is not valid Base64URL", e);
        }

        if (iv.length != 12) {
            throw new PayloadParseException("Payload IV must be 12 bytes long");
        }

        EncryptedDataKey encryptedDataKey =
                new EncryptedDataKey(parts[4], new KeyDescriptor(keyRef, "UNKNOWN"));

        try {
            ciphertext = Base64Url.decode(parts[5]);
        } catch (IllegalArgumentException e) {
            throw new PayloadParseException("Payload ciphertext is not valid Base64URL", e);
        }

        return new EncryptedPayload(version, ALGORITHM, keyRef, iv, encryptedDataKey, ciphertext);
    }

    private static void validateSegment(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new PayloadParseException(field + " must not be blank");
        }
        if (value.contains(".")) {
            throw new PayloadParseException(field + " must not contain '.' because it is a payload delimiter");
        }
    }
}
