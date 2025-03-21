package com.example.searchservice.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SearchRequestTest {

    @Test
    void testSearchRequestDefaultValues() {
        SearchRequest request = new SearchRequest();
        assertEquals(0, request.getPage(), "Default page should be 0");
        assertEquals(10, request.getSize(), "Default size should be 10");
        assertEquals("createdDate", request.getDateField(), "Default dateField should be createdDate");
    }

    @Test
    void testSearchRequestGettersAndSetters() {
        // Create a request
        SearchRequest request = new SearchRequest();

        // Test query
        String query = "test query";
        request.setQuery(query);
        assertEquals(query, request.getQuery(), "Query should match");

        // Test fields
        List<String> fields = Arrays.asList("title", "content", "author");
        request.setFields(fields);
        assertEquals(fields, request.getFields(), "Fields should match");

        // Test pagination
        int page = 2;
        request.setPage(page);
        assertEquals(page, request.getPage(), "Page should match");

        int size = 25;
        request.setSize(size);
        assertEquals(size, request.getSize(), "Size should match");

        // Test sort
        Map<String, String> sort = new HashMap<>();
        sort.put("createdDate", "desc");
        sort.put("title", "asc");
        request.setSort(sort);
        assertEquals(sort, request.getSort(), "Sort should match");

        // Test filters
        Map<String, Object> filters = new HashMap<>();
        filters.put("category", "Technology");
        filters.put("author", "John Doe");
        request.setFilters(filters);
        assertEquals(filters, request.getFilters(), "Filters should match");

        // Test date range
        String dateFrom = "2023-01-01";
        request.setDateFrom(dateFrom);
        assertEquals(dateFrom, request.getDateFrom(), "DateFrom should match");

        String dateTo = "2023-12-31";
        request.setDateTo(dateTo);
        assertEquals(dateTo, request.getDateTo(), "DateTo should match");

        String dateField = "lastUpdatedDate";
        request.setDateField(dateField);
        assertEquals(dateField, request.getDateField(), "DateField should match");

        // Test min score
        Float minScore = 0.5f;
        request.setMinScore(minScore);
        assertEquals(minScore, request.getMinScore(), "MinScore should match");
    }
}