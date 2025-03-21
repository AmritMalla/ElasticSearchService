package com.example.searchservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the ElasticsearchQueryException class.
 *
 * These tests verify the behavior of the ElasticsearchQueryException, ensuring proper
 * initialization with message and cause, as well as correct inheritance from RuntimeException.
 */
class ElasticsearchQueryExceptionTest {

    /**
     * Tests the creation of an ElasticsearchQueryException with a message and cause.
     *
     * Expected behavior: should correctly set the exception message and underlying cause.
     */
    @Test
    void testExceptionCreation() {
        String errorMessage = "Failed to execute search query";
        Throwable cause = new RuntimeException("Root cause");

        ElasticsearchQueryException exception = new ElasticsearchQueryException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage(), "Message should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }

    /**
     * Tests the inheritance hierarchy of ElasticsearchQueryException.
     *
     * Expected behavior: should confirm that the exception is an instance of RuntimeException.
     */
    @Test
    void testExceptionInheritance() {
        ElasticsearchQueryException exception = new ElasticsearchQueryException(
                "Test message", new RuntimeException()
        );

        assertTrue(exception instanceof RuntimeException, "Should be a RuntimeException");
    }
}