package com.IssueWatch.API.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorResponse {

    private String message;
    private int status;
    private Map<String, String> errors;
    private LocalDateTime timestamp;

    public ValidationErrorResponse(String message, int status, Map<String, String> errors) {
        this.errors = errors;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
