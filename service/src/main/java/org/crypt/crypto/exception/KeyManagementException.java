package org.crypt.crypto.exception;

public class KeyManagementException extends CryptoException{
    public KeyManagementException(String message, Throwable cause){
        super(message, cause);
    }

    public KeyManagementException(String message){
        super(message);
    }
}
