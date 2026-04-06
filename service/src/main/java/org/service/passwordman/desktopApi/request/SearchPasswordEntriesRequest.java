package org.service.passwordman.desktopApi.request;

public class SearchPasswordEntriesRequest {

    private int userId;
    private String titleQuery;

    public SearchPasswordEntriesRequest() {
    }

    public SearchPasswordEntriesRequest(int userId, String titleQuery) {
        this.userId = userId;
        this.titleQuery = titleQuery;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitleQuery() {
        return titleQuery;
    }
}