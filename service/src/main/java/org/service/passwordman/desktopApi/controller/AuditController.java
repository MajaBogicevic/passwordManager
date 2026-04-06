package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.AuditHandler;

public class AuditController {

    private final AuditHandler auditHandler;

    public AuditController(AuditHandler auditHandler) {
        this.auditHandler = auditHandler;
    }

    public Object getSecurityActivity() {
        return auditHandler.getSecurityActivitySafe();
    }
}