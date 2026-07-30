package org.service.passwordman.desktopApi.response;

import java.util.List;

public class PagedAuditLogResponse {

    private final boolean success = true;
    private final List<AuditLogResponse> items;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PagedAuditLogResponse(List<AuditLogResponse> items, int page, int size, long totalElements) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
    }

    public boolean isSuccess() {
        return success;
    }

    public List<AuditLogResponse> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}