package org.service.passwordman.application.port;

public interface MasterPasswordVerifier {

    boolean verify(String masterPassword);
}