package com.example.searchservice.service;

import com.example.searchservice.model.SearchRequest;
import com.example.searchservice.model.SearchResponse;
import com.example.searchservice.model.SearchableDocument;

public interface SearchService {

    /**
     * Perform a search using the provided request parameters.
     *
     * @param searchRequest Object containing search parameters
     * @return SearchResponse with results and metadata
     */
    SearchResponse<SearchableDocument> search(SearchRequest searchRequest);

    /**
     * Check Elasticsearch cluster health.
     *
     * @return Health status as String
     */
    String checkHealth();

}
