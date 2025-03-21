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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getRequestURI()).thenReturn("/api/search");
    }

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