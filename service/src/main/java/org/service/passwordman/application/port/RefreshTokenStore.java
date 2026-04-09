package org.service.passwordman.application.port;

import java.util.Optional;

public interface RefreshTokenStore {

    void save(String refreshTokenId, int userId, String tokenFamilyId, long expiresAtMillis);

    Optional<RefreshTokenRecord> findByTokenId(String refreshTokenId);

    void rotate(
            String currentRefreshTokenId,
            String newRefreshTokenId,
            int userId,
            String tokenFamilyId,
            long newExpiresAtMillis
    );

    void revoke(String refreshTokenId);

    void revokeFamily(String tokenFamilyId);

    void revokeAllByUserId(int userId);
}