package org.crypt.crypto.keys;

public interface DataKeyProviderInterface {
    String defaultKeyRef();
    DataKeyMaterial generateDataKey(KeyRequest request);
    byte[] unwrapDataKey(EncryptedDataKey encryptedDataKey, KeyRequest request);
}
