package org.service.passwordman.application.port;

public class RefreshTokenRecord {

    private final int userId;
    private final String tokenFamilyId;
    private final long expiresAtMillis;
    private final boolean revoked;
    private final boolean consumed;
    private final boolean familyRevoked;

    public RefreshTokenRecord(
            int userId,
            String tokenFamilyId,
            long expiresAtMillis,
            boolean revoked,
            boolean consumed,
            boolean familyRevoked
    ) {
        this.userId = userId;
        this.tokenFamilyId = tokenFamilyId;
        this.expiresAtMillis = expiresAtMillis;
        this.revoked = revoked;
        this.consumed = consumed;
        this.familyRevoked = familyRevoked;
    }

    public int getUserId() {
        return userId;
    }

    public String getTokenFamilyId() {
        return tokenFamilyId;
    }

    public long getExpiresAtMillis() {
        return expiresAtMillis;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public boolean isFamilyRevoked() {
        return familyRevoked;
    }

    public boolean isExpired(long nowMillis) {
        return nowMillis >= expiresAtMillis;
    }
}