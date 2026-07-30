package org.service.passwordman.application.service.audit;

import java.util.List;

import org.service.passwordman.domain.model.AuditLog;

public class AuditActivityPage {

    private final List<AuditLog> items;
    private final long totalElements;

    public AuditActivityPage(List<AuditLog> items, long totalElements) {
        this.items = items;
        this.totalElements = totalElements;
    }

    public List<AuditLog> getItems() {
        return items;
    }

    public long getTotalElements() {
        return totalElements;
    }
}