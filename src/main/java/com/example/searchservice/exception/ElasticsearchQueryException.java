package com.example.searchservice.exception;

/**
 * Exception thrown when an Elasticsearch query fails to execute properly.
 * This is a runtime exception indicating an issue with query processing or connectivity.
 */
public class ElasticsearchQueryException extends RuntimeException {

    /**
     * Constructs a new ElasticsearchQueryException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the underlying cause of the exception (e.g., an IOException or Elasticsearch-specific error)
     */
    public ElasticsearchQueryException(String message, Throwable cause) {
        super(message, cause);  // Pass message and cause to RuntimeException constructor
    }
}