package com.example.searchservice.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SearchResponse class.
 *
 * These tests verify the behavior of the SearchResponse model, ensuring proper creation
 * with all fields and correct calculation of pagination-related properties.
 */
class SearchResponseTest {

    /**
     * Tests the creation and initialization of a SearchResponse object.
     *
     * Expected behavior: should correctly set items, total hits, page, size, aggregations,
     * and took fields with the provided values.
     */
    @Test
    void testSearchResponseCreation() {
        // Create test data
        List<String> items = Arrays.asList("item1", "item2", "item3");
        long totalHits = 3;
        int page = 0;
        int size = 10;
        Map<String, Map<String, Long>> aggregations = new HashMap<>();
        long took = 42;

        // Create response
        SearchResponse<String> response = new SearchResponse<>(
                items, totalHits, page, size, aggregations, took
        );

        // Verify fields
        assertEquals(items, response.getItems(), "Items should match");
        assertEquals(totalHits, response.getTotalHits(), "TotalHits should match");
        assertEquals(page, response.getPage(), "Page should match");
        assertEquals(size, response.getSize(), "Size should match");
        assertEquals(aggregations, response.getAggregations(), "Aggregations should match");
        assertEquals(took, response.getTook(), "Took should match");
    }

    /**
     * Tests the pagination calculations in the SearchResponse class.
     *
     * Expected behavior: should accurately calculate total pages and determine if there is a next page
     * across various scenarios including single page, multiple pages, last page, middle page,
     * and zero size cases.
     */
    @Test
    void testPaginationCalculation() {
        // Case 1: Exactly one page of results
        SearchResponse<String> response1 = new SearchResponse<>(
                Arrays.asList("item1", "item2", "item3"),
                3, 0, 10, null, 10
        );
        assertEquals(1, response1.getTotalPages(), "Should calculate 1 total page");
        assertFalse(response1.isHasNext(), "Should not have next page");

        // Case 2: Multiple full pages
        SearchResponse<String> response2 = new SearchResponse<>(
                Arrays.asList("item1", "item2", "item3"),
                25, 0, 10, null, 10
        );
        assertEquals(3, response2.getTotalPages(), "Should calculate 3 total pages");
        assertTrue(response2.isHasNext(), "Should have next page");

        // Case 3: Last page
        SearchResponse<String> response3 = new SearchResponse<>(
                Arrays.asList("item1", "item2", "item3"),
                25, 2, 10, null, 10
        );
        assertEquals(3, response3.getTotalPages(), "Should calculate 3 total pages");
        assertFalse(response3.isHasNext(), "Should not have next page");

        // Case 4: Middle page
        SearchResponse<String> response4 = new SearchResponse<>(
                Arrays.asList("item1", "item2", "item3"),
                25, 1, 10, null, 10
        );
        assertEquals(3, response4.getTotalPages(), "Should calculate 3 total pages");
        assertTrue(response4.isHasNext(), "Should have next page");

        // Case 5: Zero size (should prevent division by zero)
        SearchResponse<String> response5 = new SearchResponse<>(
                Arrays.asList("item1", "item2", "item3"),
                25, 0, 0, null, 10
        );
        assertEquals(0, response5.getTotalPages(), "Should handle zero size safely");
        assertFalse(response5.isHasNext(), "Should not have next page with zero size");
    }
}