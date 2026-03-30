package org.crypt.crypto.exception;

public class PayloadParseException extends CryptoException{
    public PayloadParseException(String message){

        super(message);
    }

    public PayloadParseException(String message, Throwable cause){

        super(message, cause);
    }
}
