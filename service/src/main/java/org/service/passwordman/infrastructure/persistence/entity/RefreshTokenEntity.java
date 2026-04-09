package org.service.passwordman.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    @Column(name = "token_id", nullable = false, length = 255)
    private String tokenId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "token_family_id", nullable = false, length = 255)
    private String tokenFamilyId;

    @Column(name = "expires_at_millis", nullable = false)
    private Long expiresAtMillis;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private boolean consumed;

    @Column(name = "family_revoked", nullable = false)
    private boolean familyRevoked;

    public RefreshTokenEntity() {
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTokenFamilyId() {
        return tokenFamilyId;
    }

    public void setTokenFamilyId(String tokenFamilyId) {
        this.tokenFamilyId = tokenFamilyId;
    }

    public Long getExpiresAtMillis() {
        return expiresAtMillis;
    }

    public void setExpiresAtMillis(Long expiresAtMillis) {
        this.expiresAtMillis = expiresAtMillis;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public boolean isFamilyRevoked() {
        return familyRevoked;
    }

    public void setFamilyRevoked(boolean familyRevoked) {
        this.familyRevoked = familyRevoked;
    }
}