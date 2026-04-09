package org.service.passwordman.application.port;

public interface UserAuthInvalidationStore {

    void invalidateAllTokensForUser(int userId, long validAfterEpochMillis);

    long getTokensValidAfterForUser(int userId);
}