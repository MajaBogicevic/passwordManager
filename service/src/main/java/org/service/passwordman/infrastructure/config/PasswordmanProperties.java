package org.service.passwordman.infrastructure.config;

import org.crypt.crypto.util.Base64Url;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "passwordman")
public class PasswordmanProperties {

    private static final int MIN_JWT_SECRET_LENGTH = 32;
    private static final int AES_256_KEY_LENGTH_BYTES = 32;

    private String encryptionKeyRef;
    private String encryptionMasterKeyBase64;
    private String jwtSecret;
    private long jwtExpirationMillis;
    private long jwtAccessExpirationMillis;
    private long jwtRefreshExpirationMillis;

    private int bcryptStrength = 12;

    private int loginRateLimitMaxAttempts = 5;
    private long loginRateLimitBlockDurationMillis = 300000;

    private int refreshRateLimitMaxAttempts = 5;
    private long refreshRateLimitBlockDurationMillis = 300000;

    private int vaultUnlockRateLimitMaxAttempts = 5;
    private long vaultUnlockRateLimitBlockDurationMillis = 300000;

    public void validate() {
        validateJwtSecret();
        validateEncryptionConfiguration();

        if (jwtExpirationMillis < 0) {
            throw new IllegalStateException("passwordman.jwt-expiration-millis must not be negative.");
        }

        if (jwtAccessExpirationMillis <= 0) {
            throw new IllegalStateException("passwordman.jwt-access-expiration-millis must be greater than 0.");
        }

        if (jwtRefreshExpirationMillis <= 0) {
            throw new IllegalStateException("passwordman.jwt-refresh-expiration-millis must be greater than 0.");
        }

        if (bcryptStrength < 10 || bcryptStrength > 15) {
            throw new IllegalStateException("passwordman.bcrypt-strength must be between 10 and 15.");
        }

        validateRateLimit(loginRateLimitMaxAttempts, loginRateLimitBlockDurationMillis, "login");
        validateRateLimit(refreshRateLimitMaxAttempts, refreshRateLimitBlockDurationMillis, "refresh");
        validateRateLimit(vaultUnlockRateLimitMaxAttempts, vaultUnlockRateLimitBlockDurationMillis, "vault unlock");
    }

    private void validateJwtSecret() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("passwordman.jwt-secret must be configured.");
        }

        if (jwtSecret.length() < MIN_JWT_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "passwordman.jwt-secret must be at least " + MIN_JWT_SECRET_LENGTH + " characters long."
            );
        }
    }

    private void validateEncryptionConfiguration() {
        if (encryptionKeyRef == null || encryptionKeyRef.isBlank()) {
            throw new IllegalStateException("passwordman.encryption-key-ref must be configured.");
        }

        if (encryptionKeyRef.contains(".")) {
            throw new IllegalStateException("passwordman.encryption-key-ref must not contain '.'.");
        }

        if (encryptionMasterKeyBase64 == null || encryptionMasterKeyBase64.isBlank()) {
            throw new IllegalStateException("passwordman.encryption-master-key-base64 must be configured.");
        }

        byte[] decodedKey;
        try {
            decodedKey = Base64Url.decode(encryptionMasterKeyBase64);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("passwordman.encryption-master-key-base64 is not valid Base64Url.", ex);
        }

        if (decodedKey.length != AES_256_KEY_LENGTH_BYTES) {
            throw new IllegalStateException(
                    "passwordman.encryption-master-key-base64 must decode to exactly "
                            + AES_256_KEY_LENGTH_BYTES + " bytes."
            );
        }
    }

    private void validateRateLimit(int maxAttempts, long blockDurationMillis, String label) {
        if (maxAttempts <= 0) {
            throw new IllegalStateException(label + " rate limit max attempts must be greater than 0.");
        }

        if (blockDurationMillis <= 0) {
            throw new IllegalStateException(label + " rate limit block duration must be greater than 0.");
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

    public long getJwtAccessExpirationMillis() {
        return jwtAccessExpirationMillis;
    }

    public void setJwtAccessExpirationMillis(long jwtAccessExpirationMillis) {
        this.jwtAccessExpirationMillis = jwtAccessExpirationMillis;
    }

    public long getJwtRefreshExpirationMillis() {
        return jwtRefreshExpirationMillis;
    }

    public void setJwtRefreshExpirationMillis(long jwtRefreshExpirationMillis) {
        this.jwtRefreshExpirationMillis = jwtRefreshExpirationMillis;
    }

    public int getBcryptStrength() {
        return bcryptStrength;
    }

    public void setBcryptStrength(int bcryptStrength) {
        this.bcryptStrength = bcryptStrength;
    }

    public int getLoginRateLimitMaxAttempts() {
        return loginRateLimitMaxAttempts;
    }

    public void setLoginRateLimitMaxAttempts(int loginRateLimitMaxAttempts) {
        this.loginRateLimitMaxAttempts = loginRateLimitMaxAttempts;
    }

    public long getLoginRateLimitBlockDurationMillis() {
        return loginRateLimitBlockDurationMillis;
    }

    public void setLoginRateLimitBlockDurationMillis(long loginRateLimitBlockDurationMillis) {
        this.loginRateLimitBlockDurationMillis = loginRateLimitBlockDurationMillis;
    }

    public int getRefreshRateLimitMaxAttempts() {
        return refreshRateLimitMaxAttempts;
    }

    public void setRefreshRateLimitMaxAttempts(int refreshRateLimitMaxAttempts) {
        this.refreshRateLimitMaxAttempts = refreshRateLimitMaxAttempts;
    }

    public long getRefreshRateLimitBlockDurationMillis() {
        return refreshRateLimitBlockDurationMillis;
    }

    public void setRefreshRateLimitBlockDurationMillis(long refreshRateLimitBlockDurationMillis) {
        this.refreshRateLimitBlockDurationMillis = refreshRateLimitBlockDurationMillis;
    }

    public int getVaultUnlockRateLimitMaxAttempts() {
        return vaultUnlockRateLimitMaxAttempts;
    }

    public void setVaultUnlockRateLimitMaxAttempts(int vaultUnlockRateLimitMaxAttempts) {
        this.vaultUnlockRateLimitMaxAttempts = vaultUnlockRateLimitMaxAttempts;
    }

    public long getVaultUnlockRateLimitBlockDurationMillis() {
        return vaultUnlockRateLimitBlockDurationMillis;
    }

    public void setVaultUnlockRateLimitBlockDurationMillis(long vaultUnlockRateLimitBlockDurationMillis) {
        this.vaultUnlockRateLimitBlockDurationMillis = vaultUnlockRateLimitBlockDurationMillis;
    }
}