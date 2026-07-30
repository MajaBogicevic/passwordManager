package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.AuditHandler;

public class AuditController {

    private final AuditHandler auditHandler;

    public AuditController(AuditHandler auditHandler) {
        this.auditHandler = auditHandler;
    }

    public Object getSecurityActivity(
            String eventType,
            String outcome,
            String fromDate,
            String toDate,
            int page,
            int size
    ) {
        return auditHandler.getSecurityActivitySafe(eventType, outcome, fromDate, toDate, page, size);
    }
}