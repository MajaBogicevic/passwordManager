package org.service.passwordman.desktopApi.request;

public class RegisterRequest {
    private String email;
    private String username;
    private String password;
    private String notes;

    public RegisterRequest() {
    }

    public RegisterRequest(
            String email,
            String username,
            String password,
            String notes
    ) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.notes = notes;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNotes() {
        return notes;
    }
}