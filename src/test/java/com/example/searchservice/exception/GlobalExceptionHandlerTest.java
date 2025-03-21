package com.example.searchservice.exception;

import com.example.searchservice.model.ErrorResponse;
import org.elasticsearch.ElasticsearchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the GlobalExceptionHandler class.
 *
 * These tests verify the behavior of the exception handler, ensuring it correctly handles
 * specific exceptions like ElasticsearchQueryException, ElasticsearchException, and generic exceptions,
 * returning appropriate ResponseEntity objects with ErrorResponse payloads.
 */
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    /**
     * Sets up the test environment before each test case.
     *
     * Initializes Mockito annotations and configures the mock HttpServletRequest to return a fixed request URI.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getRequestURI()).thenReturn("/api/search");
    }

    /**
     * Tests the handling of an ElasticsearchQueryException.
     *
     * Expected behavior: should return a ResponseEntity with HTTP 500 status and an ErrorResponse
     * containing the exception message and request path.
     */
    @Test
    void testHandleElasticsearchQueryException() {
        // Arrange
        ElasticsearchQueryException ex = new ElasticsearchQueryException("Query failed", new RuntimeException("Connection error"));

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleElasticsearchQueryException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error executing search query: Query failed", response.getBody().getMessage());
        assertEquals("/api/search", response.getBody().getPath());
    }

    /**
     * Tests the handling of a generic ElasticsearchException.
     *
     * Expected behavior: should return a ResponseEntity with HTTP 500 status and an ErrorResponse
     * containing the exception message prefixed with a generic Elasticsearch error indicator.
     */
    @Test
    void testHandleElasticsearchException() {
        // Arrange
        ElasticsearchException ex = new ElasticsearchException("Index not found");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleElasticsearchException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Elasticsearch error: Index not found", response.getBody().getMessage());
    }

    /**
     * Tests the handling of a generic Exception.
     *
     * Expected behavior: should return a ResponseEntity with HTTP 500 status and an ErrorResponse
     * containing a generic error message combined with the exception's message.
     */
    @Test
    void testHandleGenericException() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error occurred");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: Unexpected error occurred", response.getBody().getMessage());
    }
}