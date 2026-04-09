package org.service.passwordman.application.security;

import org.service.passwordman.domain.model.SecurityEventOutcome;
import org.service.passwordman.domain.model.SecurityEventType;

public class SecurityAuditEvent {

    private final int userId;
    private final SecurityEventType eventType;
    private final SecurityEventOutcome outcome;
    private final String reasonCode;
    private final String ipAddress;
    private final String sessionId;
    private final String details;

    public SecurityAuditEvent(
            int userId,
            SecurityEventType eventType,
            SecurityEventOutcome outcome,
            String reasonCode,
            String ipAddress,
            String sessionId,
            String details
    ) {
        this.userId = userId;
        this.eventType = eventType;
        this.outcome = outcome;
        this.reasonCode = reasonCode;
        this.ipAddress = ipAddress;
        this.sessionId = sessionId;
        this.details = details;
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

    public static SecurityAuditEvent success(
            int userId,
            SecurityEventType eventType,
            String ipAddress,
            String sessionId,
            String details
    ) {
        return new SecurityAuditEvent(
                userId,
                eventType,
                SecurityEventOutcome.SUCCESS,
                null,
                ipAddress,
                sessionId,
                details
        );
    }

    public static SecurityAuditEvent failure(
            int userId,
            SecurityEventType eventType,
            String reasonCode,
            String ipAddress,
            String sessionId,
            String details
    ) {
        return new SecurityAuditEvent(
                userId,
                eventType,
                SecurityEventOutcome.FAILURE,
                reasonCode,
                ipAddress,
                sessionId,
                details
        );
    }

    public static SecurityAuditEvent rateLimited(
            int userId,
            SecurityEventType eventType,
            String ipAddress,
            String sessionId,
            String details
    ) {
        return new SecurityAuditEvent(
                userId,
                eventType,
                SecurityEventOutcome.RATE_LIMITED,
                "TOO_MANY_ATTEMPTS",
                ipAddress,
                sessionId,
                details
        );
    }

    public static SecurityAuditEvent securityAlert(
            int userId,
            SecurityEventType eventType,
            String reasonCode,
            String ipAddress,
            String sessionId,
            String details
    ) {
        return new SecurityAuditEvent(
                userId,
                eventType,
                SecurityEventOutcome.SECURITY_ALERT,
                reasonCode,
                ipAddress,
                sessionId,
                details
        );
    }

    public static SecurityAuditEvent legacyBusinessAction(
            int userId,
            String action,
            String ipAddress
    ) {
        return new SecurityAuditEvent(
                userId,
                SecurityEventType.BUSINESS_ACTION,
                SecurityEventOutcome.SUCCESS,
                action,
                ipAddress,
                null,
                action
        );
    }
}