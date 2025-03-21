package com.example.searchservice.exception;

import com.example.searchservice.model.ErrorResponse;
import org.elasticsearch.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ElasticsearchQueryException and returns a 500 Internal Server Error response.
     *
     * @param ex ElasticsearchQueryException thrown by the application
     * @param request Current HTTP request
     * @return ResponseEntity with error information
     */
    @ExceptionHandler(ElasticsearchQueryException.class)
    public ResponseEntity<ErrorResponse> handleElasticsearchQueryException(
            ElasticsearchQueryException ex, HttpServletRequest request) {
        
        logger.error("Elasticsearch query error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error executing search query: " + ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles ElasticsearchException and returns a 500 Internal Server Error response.
     *
     * @param ex ElasticsearchException thrown by Elasticsearch client
     * @param request Current HTTP request
     * @return ResponseEntity with error information
     */
    @ExceptionHandler(ElasticsearchException.class)
    public ResponseEntity<ErrorResponse> handleElasticsearchException(
            ElasticsearchException ex, HttpServletRequest request) {
        
        logger.error("Elasticsearch error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Elasticsearch error: " + ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles validation errors and returns a 400 Bad Request response.
     *
     * @param ex MethodArgumentNotValidException thrown during request validation
     * @param request Current HTTP request
     * @return ResponseEntity with detailed validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        logger.error("Validation error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                request.getRequestURI()
        );
        
        // Add individual field errors to the response
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorResponse.addValidationError(error.getField(), error.getDefaultMessage());
        });
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback handler for all other exceptions.
     *
     * @param ex Exception thrown by the application
     * @param request Current HTTP request
     * @return ResponseEntity with error information
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}