package com.example.searchservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * Represents a search request with query parameters and filtering options.
 */
@Getter
@Setter
public class SearchRequest {

    /**
     * The main search query string.
     */
    @NotBlank(message = "Search query is required")
    @Size(min = 2, message = "Search query must be at least 2 characters")
    private String query;

    /**
     * List of fields to search within. If empty or null, search will be performed across all available fields.
     */
    private List<String> fields;

    /**
     * Current page number for pagination, starting from 0.
     */
    @Min(value = 0, message = "Page number must be non-negative")
    private int page = 0;  // Default to first page

    /**
     * Number of results to return per page.
     */
    @Min(value = 1, message = "Page size must be positive")
    private int size = 10;  // Default to 10 results per page

    /**
     * Map specifying fields to sort by and their sort directions ("asc" or "desc").
     */
    private Map<String, String> sort;

    /**
     * Map of field names to filter values for exact matching.
     */
    private Map<String, Object> filters;

    /**
     * Start date for date range filtering in ISO format (e.g., "2023-01-01").
     */
    private String dateFrom;

    /**
     * End date for date range filtering in ISO format (e.g., "2023-12-31").
     */
    private String dateTo;

    /**
     * Field name to apply the date range filter to.
     */
    private String dateField = "createdDate";  // Default date field for filtering

    /**
     * Minimum relevance score for results to be included in the response.
     */
    private Float minScore;
}