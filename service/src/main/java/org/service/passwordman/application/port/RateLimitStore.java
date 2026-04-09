package org.service.passwordman.application.port;

public interface RateLimitStore {

    boolean isBlocked(String key);

    void recordFailure(String key, int maxAttempts, long blockDurationMillis);

    void reset(String key);

    int getFailureCount(String key);
}