package org.crypt.crypto.exception;

public class DecryptionException extends CryptoException{
    public DecryptionException(String message, Throwable cause){
        super(message, cause);
    }

    public DecryptionException(String message){
        super(message);
    }
}
