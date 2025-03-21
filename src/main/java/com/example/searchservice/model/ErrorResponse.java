package com.example.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {

    /**
     * Timestamp when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code.
     */
    private int status;

    /**
     * Error message.
     */
    private String message;

    /**
     * Path of the request that caused the error.
     */
    private String path;

    /**
     * List of validation errors.
     */
    private List<ValidationError> errors = new ArrayList<>();

    /**
     * Constructor for creating an error response.
     *
     * @param status HTTP status code
     * @param message Error message
     * @param path Request path
     */
    public ErrorResponse(int status, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.path = path;
    }

    /**
     * Adds a validation error to the error list.
     *
     * @param field Field with validation error
     * @param message Error message
     */
    public void addValidationError(String field, String message) {
        this.errors.add(new ValidationError(field, message));
    }

    /**
     * Inner class representing a single validation error.
     */
    @Getter
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;

    }

}
