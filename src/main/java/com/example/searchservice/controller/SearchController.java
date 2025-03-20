package com.example.searchservice.controller;

import com.example.searchservice.model.SearchRequest;
import com.example.searchservice.model.SearchResponse;
import com.example.searchservice.model.SearchableDocument;
import com.example.searchservice.service.SearchService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Health check endpoint to verify the service and Elasticsearch are operational.
     *
     * @return Health status information
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check requested");
        String health = searchService.checkHealth();
        return ResponseEntity.ok(health);
    }

    /**
     * Endpoint for basic search operations.
     *
     * @param searchRequest Search parameters
     * @return Search results with pagination
     */
    @PostMapping("/search")
    public ResponseEntity<SearchResponse<SearchableDocument>> search(
            @Valid @RequestBody SearchRequest searchRequest) {
        
        logger.info("Search requested with query: {}", searchRequest.getQuery());
        
        // Record start time for performance logging
        long startTime = System.currentTimeMillis();
        
        // Execute the search
        SearchResponse<SearchableDocument> response = searchService.search(searchRequest);
        
        // Log the search performance
        logger.info("Search completed in {} ms with {} results", 
                System.currentTimeMillis() - startTime, response.getTotalHits());
        
        return ResponseEntity.ok(response);
    }

}