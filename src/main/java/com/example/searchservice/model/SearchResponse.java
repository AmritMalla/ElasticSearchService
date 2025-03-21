package com.example.searchservice.model;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class SearchResponse<T> {

    /**
     * List of search result items.
     */
    private List<T> items;

    /**
     * Total number of matching results.
     */
    private long totalHits;

    /**
     * Current page number.
     */
    private int page;

    /**
     * Number of results per page.
     */
    private int size;

    /**
     * Total number of available pages.
     */
    private int totalPages;

    /**
     * Flag indicating if there are more pages available.
     */
    private boolean hasNext;

    /**
     * Aggregated information from the search results.
     */
    private Map<String, Map<String, Long>> aggregations;

    /**
     * Time taken to execute the search in milliseconds.
     */
    private long took;

    /**
     * Constructor for creating a search response with results and metadata.
     *
     * @param items        List of search result items
     * @param totalHits    Total number of matching results
     * @param page         Current page number
     * @param size         Number of results per page
     * @param aggregations Map of aggregation results
     * @param took         Time taken to execute the search
     */
    public SearchResponse(List<T> items, long totalHits, int page, int size,
                          Map<String, Map<String, Long>> aggregations, long took) {
        this.items = items;
        this.totalHits = totalHits;
        this.page = page;
        this.size = size;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalHits / size) : 0;
        this.hasNext = page + 1 < this.totalPages;
        this.aggregations = aggregations;
        this.took = took;
    }

}