package com.example.searchservice.exception;

import com.example.searchservice.model.ErrorResponse;
import org.elasticsearch.ElasticsearchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    private final String TEST_URI = "/api/search";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(TEST_URI);
    }

    @Test
    void testHandleElasticsearchQueryException() {
        // Setup
        String errorMessage = "Failed to parse query";
        ElasticsearchQueryException exception = new ElasticsearchQueryException(
                errorMessage, new RuntimeException("Root cause")
        );

        // Execute
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleElasticsearchQueryException(exception, request);

        // Verify
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be 500");

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response should not be null");
        assertEquals(500, errorResponse.getStatus(), "Status code should be 500");
        assertTrue(errorResponse.getMessage().contains(errorMessage), "Message should contain the error");
        assertEquals(TEST_URI, errorResponse.getPath(), "Path should match request URI");
    }

    @Test
    void testHandleElasticsearchException() {
        // Setup
        String errorMessage = "Elasticsearch cluster error";
        ElasticsearchException exception = new ElasticsearchException(errorMessage);

        // Execute
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleElasticsearchException(exception, request);

        // Verify
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be 500");

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response should not be null");
        assertEquals(500, errorResponse.getStatus(), "Status code should be 500");
        assertTrue(errorResponse.getMessage().contains(errorMessage), "Message should contain the error");
        assertEquals(TEST_URI, errorResponse.getPath(), "Path should match request URI");
    }

    @Test
    void testHandleValidationExceptions() {
        // Setup field errors
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("searchRequest", "query", "Query is required"));
        fieldErrors.add(new FieldError("searchRequest", "size", "Size must be positive"));

        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Execute
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(validationException, request);

        // Verify
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be 400");

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response should not be null");
        assertEquals(400, errorResponse.getStatus(), "Status code should be 400");
        assertEquals("Validation error", errorResponse.getMessage(), "Message should indicate validation error");
        assertEquals(TEST_URI, errorResponse.getPath(), "Path should match request URI");

        // Verify field errors
        assertEquals(2, errorResponse.getErrors().size(), "Should have 2 validation errors");
        assertEquals("query", errorResponse.getErrors().get(0).getField(), "First error field should be query");
        assertEquals("Query is required", errorResponse.getErrors().get(0).getMessage(), "First error message should match");
        assertEquals("size", errorResponse.getErrors().get(1).getField(), "Second error field should be size");
        assertEquals("Size must be positive", errorResponse.getErrors().get(1).getMessage(), "Second error message should match");
    }

    @Test
    void testHandleGenericException() {
        // Setup
        String errorMessage = "Unexpected runtime error";
        Exception exception = new RuntimeException(errorMessage);

        // Execute
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception, request);

        // Verify
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be 500");

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response should not be null");
        assertEquals(500, errorResponse.getStatus(), "Status code should be 500");
        assertTrue(errorResponse.getMessage().contains(errorMessage), "Message should contain the error");
        assertEquals(TEST_URI, errorResponse.getPath(), "Path should match request URI");
    }
}