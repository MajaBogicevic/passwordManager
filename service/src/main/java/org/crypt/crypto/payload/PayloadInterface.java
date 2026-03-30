package org.crypt.crypto.payload;

public interface PayloadInterface {
    String encode(EncryptedPayload payload);
    EncryptedPayload decode(String payload);

}
