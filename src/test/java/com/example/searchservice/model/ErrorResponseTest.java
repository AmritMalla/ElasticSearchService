package com.example.searchservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ErrorResponse class.
 *
 * These tests verify the behavior of the ErrorResponse model, ensuring proper creation,
 * field initialization, and management of validation errors.
 */
class ErrorResponseTest {

    /**
     * Tests the creation and initialization of an ErrorResponse object.
     *
     * Expected behavior: should correctly set status, message, path, and timestamp fields,
     * with an initially empty errors list and a recent timestamp.
     */
    @Test
    void testErrorResponseCreation() {
        // Create error response
        int status = 400;
        String message = "Bad request";
        String path = "/api/search";
        ErrorResponse response = new ErrorResponse(status, message, path);

        // Verify fields
        assertEquals(status, response.getStatus(), "Status should match");
        assertEquals(message, response.getMessage(), "Message should match");
        assertEquals(path, response.getPath(), "Path should match");
        assertNotNull(response.getTimestamp(), "Timestamp should not be null");

        // Timestamp should be very recent (within last second)
        long secondsDiff = ChronoUnit.SECONDS.between(response.getTimestamp(), LocalDateTime.now());
        assertTrue(secondsDiff < 2, "Timestamp should be recent");

        // Errors list should be empty initially
        assertNotNull(response.getErrors(), "Errors list should not be null");
        assertEquals(0, response.getErrors().size(), "Errors list should be empty");
    }

    /**
     * Tests the addition of validation errors to an ErrorResponse object.
     *
     * Expected behavior: should correctly add and store validation errors with their respective
     * field names and messages in the errors list.
     */
    @Test
    void testAddValidationError() {
        // Create error response
        ErrorResponse response = new ErrorResponse(400, "Bad request", "/api/search");

        // Add validation errors
        response.addValidationError("query", "Query is required");
        response.addValidationError("size", "Size must be positive");

        // Verify errors
        assertEquals(2, response.getErrors().size(), "Should have 2 validation errors");

        ErrorResponse.ValidationError error1 = response.getErrors().get(0);
        assertEquals("query", error1.getField(), "Field name should match");
        assertEquals("Query is required", error1.getMessage(), "Error message should match");

        ErrorResponse.ValidationError error2 = response.getErrors().get(1);
        assertEquals("size", error2.getField(), "Field name should match");
        assertEquals("Size must be positive", error2.getMessage(), "Error message should match");
    }

    /**
     * Tests the creation of a ValidationError inner class instance.
     *
     * Expected behavior: should correctly initialize the field and message properties of the ValidationError.
     */
    @Test
    void testValidationErrorCreation() {
        String field = "query";
        String message = "Query is required";
        ErrorResponse.ValidationError error = new ErrorResponse.ValidationError(field, message);

        assertEquals(field, error.getField(), "Field should match");
        assertEquals(message, error.getMessage(), "Message should match");
    }
}