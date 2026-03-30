package org.service.passwordman.domain.model;

import java.time.LocalDateTime;

public class AuditLog {
    private int id;
    private int userId;
    private String action;
    private String ip;
    private LocalDateTime timestamp;

    public AuditLog(int id, int userId, String action, String ip, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.ip = ip;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getIp() {
        return ip;
    }


    public String getAction() {
        return action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}