package org.service.passwordman.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "passwordman")
public class PasswordmanProperties {

    private String encryptionKeyRef;
    private String encryptionMasterKeyBase64;
    private String jwtSecret;
    private long jwtExpirationMillis;
    private String allowedOrigins;

    public void validate() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("passwordman.jwt-secret must be configured.");
        }

        if (jwtSecret.length() < 32) {
            throw new IllegalStateException("passwordman.jwt-secret must be at least 32 characters long.");
        }

        if (jwtExpirationMillis <= 0) {
            throw new IllegalStateException("passwordman.jwt-expiration-millis must be greater than 0.");
        }
    }

    public String getEncryptionKeyRef() {
        return encryptionKeyRef;
    }

    public void setEncryptionKeyRef(String encryptionKeyRef) {
        this.encryptionKeyRef = encryptionKeyRef;
    }

    public String getEncryptionMasterKeyBase64() {
        return encryptionMasterKeyBase64;
    }

    public void setEncryptionMasterKeyBase64(String encryptionMasterKeyBase64) {
        this.encryptionMasterKeyBase64 = encryptionMasterKeyBase64;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public long getJwtExpirationMillis() {
        return jwtExpirationMillis;
    }

    public void setJwtExpirationMillis(long jwtExpirationMillis) {
        this.jwtExpirationMillis = jwtExpirationMillis;
    }
}