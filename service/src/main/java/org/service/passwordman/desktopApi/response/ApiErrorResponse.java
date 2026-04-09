package org.service.passwordman.desktopApi.response;

public class ApiErrorResponse {

    private String code;
    private String message;
    private Object details;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String code, String message) {
        this(code, message, null);
    }

    public ApiErrorResponse(String code, String message, Object details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getDetails() {
        return details;
    }
}