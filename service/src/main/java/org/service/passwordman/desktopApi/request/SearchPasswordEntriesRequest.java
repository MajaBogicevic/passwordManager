package org.service.passwordman.desktopApi.request;

public class SearchPasswordEntriesRequest {
    private String titleQuery;

    public SearchPasswordEntriesRequest() {
    }

    public SearchPasswordEntriesRequest(String titleQuery) {
        this.titleQuery = titleQuery;
    }

    public String getTitleQuery() {
        return titleQuery;
    }
}