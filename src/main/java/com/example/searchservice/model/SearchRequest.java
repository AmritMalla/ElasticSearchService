package com.example.searchservice.model;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

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
     * Fields to search within. If empty, will search in all fields.
     */
    private List<String> fields;

    /**
     * Current page for pagination.
     */
    @Min(value = 0, message = "Page number must be non-negative")
    private int page = 0;

    /**
     * Number of results per page.
     */
    @Min(value = 1, message = "Page size must be positive")
    private int size = 10;

    /**
     * Map of field names to sort directions ("asc" or "desc").
     */
    private Map<String, String> sort;

    /**
     * Map of field names to filter values for exact matches.
     */
    private Map<String, Object> filters;

    /**
     * Date range filter (from date in ISO format).
     */
    private String dateFrom;

    /**
     * Date range filter (to date in ISO format).
     */
    private String dateTo;

    /**
     * Field to apply date range filter to.
     */
    private String dateField = "createdDate";

    /**
     * Minimum score for results to be included.
     */
    private Float minScore;

}