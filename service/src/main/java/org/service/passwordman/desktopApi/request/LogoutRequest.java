package org.service.passwordman.desktopApi.request;

public class LogoutRequest {

    private boolean allSessions;

    public LogoutRequest() {
    }

    public boolean isAllSessions() {
        return allSessions;
    }

    public void setAllSessions(boolean allSessions) {
        this.allSessions = allSessions;
    }
}