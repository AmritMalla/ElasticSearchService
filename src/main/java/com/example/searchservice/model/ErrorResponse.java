package com.example.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an error response containing details about an error that occurred during request processing.
 */
@Getter
@Setter
public class ErrorResponse {

    /**
     * Timestamp when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code associated with the error.
     */
    private int status;

    /**
     * Descriptive message about the error.
     */
    private String message;

    /**
     * URL path of the request that caused the error.
     */
    private String path;

    /**
     * List of validation errors associated with the request.
     */
    private List<ValidationError> errors = new ArrayList<>();

    /**
     * Constructs a new ErrorResponse with the specified status, message, and path.
     *
     * @param status the HTTP status code
     * @param message the error message
     * @param path the request path that triggered the error
     */
    public ErrorResponse(int status, String message, String path) {
        this.timestamp = LocalDateTime.now();  // Set current timestamp when error occurs
        this.status = status;
        this.message = message;
        this.path = path;
    }

    /**
     * Adds a validation error to the list of errors.
     *
     * @param field the field that failed validation
     * @param message the validation error message
     */
    public void addValidationError(String field, String message) {
        this.errors.add(new ValidationError(field, message));  // Append new validation error to the list
    }

    /**
     * Represents a single validation error with its field and message.
     */
    @Getter
    @AllArgsConstructor
    public static class ValidationError {
        /**
         * The field that failed validation.
         */
        private String field;

        /**
         * The validation error message.
         */
        private String message;
    }
}