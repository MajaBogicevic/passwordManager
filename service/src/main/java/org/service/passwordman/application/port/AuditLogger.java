package org.service.passwordman.application.port;

import org.service.passwordman.application.security.SecurityAuditEvent;

public interface AuditLogger {

    void log(SecurityAuditEvent event);

    default void log(int userId, String action, String ipAddress) {
        log(SecurityAuditEvent.legacyBusinessAction(userId, action, ipAddress));
    }
}