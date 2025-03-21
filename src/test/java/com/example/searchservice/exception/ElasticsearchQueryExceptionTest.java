package com.example.searchservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElasticsearchQueryExceptionTest {

    @Test
    void testExceptionCreation() {
        String errorMessage = "Failed to execute search query";
        Throwable cause = new RuntimeException("Root cause");

        ElasticsearchQueryException exception = new ElasticsearchQueryException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage(), "Message should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }

    @Test
    void testExceptionInheritance() {
        ElasticsearchQueryException exception = new ElasticsearchQueryException(
                "Test message", new RuntimeException()
        );

        assertTrue(exception instanceof RuntimeException, "Should be a RuntimeException");
    }
}