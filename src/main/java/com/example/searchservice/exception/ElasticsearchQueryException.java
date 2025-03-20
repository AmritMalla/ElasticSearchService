package com.example.searchservice.exception;

public class ElasticsearchQueryException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public ElasticsearchQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}