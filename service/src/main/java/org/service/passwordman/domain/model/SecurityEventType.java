package org.service.passwordman.domain.model;

public enum SecurityEventType {
    BUSINESS_ACTION(false),
    USER_REGISTERED(true),
    LOGIN(true),
    REFRESH_TOKEN(true),
    LOGOUT(true),
    PASSWORD_CHANGED(true),
    VAULT_UNLOCK(true),
    VAULT_LOCK(true),
    VAULT_AUTO_LOCK(true),
    PASSWORD_COPIED(true);

    private final boolean securityEvent;

    SecurityEventType(boolean securityEvent) {
        this.securityEvent = securityEvent;
    }

    public boolean isSecurityEvent() {
        return securityEvent;
    }
}