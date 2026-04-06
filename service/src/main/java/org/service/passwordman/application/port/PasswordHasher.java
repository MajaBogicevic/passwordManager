package org.service.passwordman.application.port;

public interface PasswordHasher {

    String hash(String password);

    boolean matches(String password, String hash);
}