package org.service.passwordman.domain.model;

import java.time.LocalDateTime;

public class AuditLog {

    private final int id;
    private final int userId;
    private final SecurityEventType eventType;
    private final SecurityEventOutcome outcome;
    private final String reasonCode;
    private final String ipAddress;
    private final String sessionId;
    private final String details;
    private final LocalDateTime timestamp;

    public AuditLog(
            int id,
            int userId,
            SecurityEventType eventType,
            SecurityEventOutcome outcome,
            String reasonCode,
            String ipAddress,
            String sessionId,
            String details,
            LocalDateTime timestamp
    ) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.outcome = outcome;
        this.reasonCode = reasonCode;
        this.ipAddress = ipAddress;
        this.sessionId = sessionId;
        this.details = details;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public SecurityEventType getEventType() {
        return eventType;
    }

    public SecurityEventOutcome getOutcome() {
        return outcome;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}