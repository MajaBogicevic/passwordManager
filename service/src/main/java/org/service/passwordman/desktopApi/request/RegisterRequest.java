package org.service.passwordman.desktopApi.request;

public class RegisterRequest {
    private String email;
    private String username;
    private String loginPassword;
    private String masterPassword;
    private String notes;

    public RegisterRequest() {
    }

    public RegisterRequest(
            String email,
            String username,
            String loginPassword,
            String masterPassword,
            String notes
    ) {
        this.email = email;
        this.username = username;
        this.loginPassword = loginPassword;
        this.masterPassword = masterPassword;
        this.notes = notes;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public String getNotes() {
        return notes;
    }
}