package org.service.passwordman.application.port;

public interface AuditLogger {

    void log(int userId, String action, String ip);
}