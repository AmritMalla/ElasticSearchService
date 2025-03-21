package com.example.searchservice.controller;

import com.example.searchservice.model.SearchRequest;
import com.example.searchservice.model.SearchResponse;
import com.example.searchservice.model.SearchableDocument;
import com.example.searchservice.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller that handles search-related API endpoints.
 * Provides basic search functionality and service health checks.
 */
@RestController
@RequestMapping("/api")
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    /**
     * Search service to handle search operations.
     */
    private final SearchService searchService;

    /**
     * Constructor for dependency injection.
     *
     * @param searchService Service for search operations
     */
    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * GET /api/health
     *
     * Health check endpoint to verify the service and Elasticsearch are operational.
     *
     * @return Health status string wrapped in ResponseEntity
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check requested");

        // Delegate to the service to get Elasticsearch health
        String health = searchService.checkHealth();

        return ResponseEntity.ok(health);
    }

    /**
     * POST /api/search
     *
     * Endpoint for executing a search query against the Elasticsearch index.
     *
     * @param searchRequest Validated search query and pagination info
     * @return Search response containing a list of matching documents and metadata
     */
    @PostMapping("/search")
    public ResponseEntity<SearchResponse<SearchableDocument>> search(
            @Valid @RequestBody SearchRequest searchRequest) {

        // Log the incoming query
        logger.info("Search requested with query: {}", searchRequest.getQuery());

        // Record start time for performance logging
        long startTime = System.currentTimeMillis();

        // Perform the search operation
        SearchResponse<SearchableDocument> response = searchService.search(searchRequest);

        // Log duration and result count
        logger.info("Search completed in {} ms with {} results",
                System.currentTimeMillis() - startTime, response.getTotalHits());

        return ResponseEntity.ok(response);
    }
}
