package org.service.passwordman.desktopApi.response;

import java.util.List;

public class PasswordEntrySearchResponse {

    private final List<PasswordEntryListItemResponse> items;

    public PasswordEntrySearchResponse(List<PasswordEntryListItemResponse> items) {
        this.items = items;
    }

    public List<PasswordEntryListItemResponse> getItems() {
        return items;
    }
}