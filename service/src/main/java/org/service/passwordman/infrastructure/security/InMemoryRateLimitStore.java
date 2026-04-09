package org.service.passwordman.infrastructure.security;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.service.passwordman.application.port.RateLimitStore;

public class InMemoryRateLimitStore implements RateLimitStore {

    private static class AttemptRecord {
        private int failureCount;
        private long blockedUntilEpochMillis;

        private AttemptRecord() {
            this.failureCount = 0;
            this.blockedUntilEpochMillis = 0L;
        }
    }

    private final ConcurrentMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    @Override
    public boolean isBlocked(String key) {
        AttemptRecord record = attempts.get(key);
        if (record == null) {
            return false;
        }

        long now = Instant.now().toEpochMilli();

        synchronized (record) {
            if (record.blockedUntilEpochMillis <= 0) {
                return false;
            }

            if (record.blockedUntilEpochMillis <= now) {
                attempts.remove(key, record);
                return false;
            }

            return true;
        }
    }

    @Override
    public void recordFailure(String key, int maxAttempts, long blockDurationMillis) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be greater than 0.");
        }

        if (blockDurationMillis <= 0) {
            throw new IllegalArgumentException("blockDurationMillis must be greater than 0.");
        }

        AttemptRecord record = attempts.computeIfAbsent(key, ignored -> new AttemptRecord());
        long now = Instant.now().toEpochMilli();

        synchronized (record) {
            if (record.blockedUntilEpochMillis > 0 && record.blockedUntilEpochMillis <= now) {
                record.failureCount = 0;
                record.blockedUntilEpochMillis = 0L;
            }

            record.failureCount++;

            if (record.failureCount >= maxAttempts) {
                record.blockedUntilEpochMillis = now + blockDurationMillis;
            }
        }
    }

    @Override
    public void reset(String key) {
        attempts.remove(key);
    }

    @Override
    public int getFailureCount(String key) {
        AttemptRecord record = attempts.get(key);
        if (record == null) {
            return 0;
        }

        long now = Instant.now().toEpochMilli();

        synchronized (record) {
            if (record.blockedUntilEpochMillis > 0 && record.blockedUntilEpochMillis <= now) {
                attempts.remove(key, record);
                return 0;
            }

            return record.failureCount;
        }
    }
}