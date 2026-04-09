package org.service.passwordman.infrastructure.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "vault_sessions")
@IdClass(VaultSessionEntity.VaultSessionId.class)
public class VaultSessionEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Id
    @Column(name = "jwt_token_id", nullable = false, length = 255)
    private String jwtTokenId;

    @Column(name = "last_activity_at", nullable = false)
    private LocalDateTime lastActivityAt;

    public VaultSessionEntity() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getJwtTokenId() {
        return jwtTokenId;
    }

    public void setJwtTokenId(String jwtTokenId) {
        this.jwtTokenId = jwtTokenId;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public static class VaultSessionId implements Serializable {
        private Integer userId;
        private String jwtTokenId;

        public VaultSessionId() {
        }

        public VaultSessionId(Integer userId, String jwtTokenId) {
            this.userId = userId;
            this.jwtTokenId = jwtTokenId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getJwtTokenId() {
            return jwtTokenId;
        }

        public void setJwtTokenId(String jwtTokenId) {
            this.jwtTokenId = jwtTokenId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VaultSessionId that)) return false;
            return Objects.equals(userId, that.userId) && Objects.equals(jwtTokenId, that.jwtTokenId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, jwtTokenId);
        }
    }
}