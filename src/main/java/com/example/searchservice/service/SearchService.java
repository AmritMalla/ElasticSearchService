package com.example.searchservice.service;

import com.example.searchservice.model.SearchRequest;
import com.example.searchservice.model.SearchResponse;
import com.example.searchservice.model.SearchableDocument;

/**
 * Service interface defining search operations and health checks for the search system.
 */
public interface SearchService {

    /**
     * Performs a search operation based on the provided request parameters.
     *
     * @param searchRequest the search request containing query parameters and filters
     * @return a SearchResponse containing the search results and metadata
     */
    SearchResponse<SearchableDocument> search(SearchRequest searchRequest);

    /**
     * Checks the health status of the Elasticsearch cluster.
     *
     * @return a string representing the cluster health status (e.g., "green", "yellow", "red")
     */
    String checkHealth();
}