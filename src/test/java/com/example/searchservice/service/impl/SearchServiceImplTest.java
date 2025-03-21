package com.example.searchservice.service.impl;

import com.example.searchservice.exception.ElasticsearchQueryException;
import com.example.searchservice.model.SearchRequest;
import com.example.searchservice.model.SearchResponse;
import com.example.searchservice.model.SearchableDocument;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private SearchHits<SearchableDocument> searchHits;

    @Mock
    private SearchHit<SearchableDocument> searchHit;

    private SearchServiceImpl searchService;

    private final String INDEX_NAME = "test-index";

    @BeforeEach
    void setUp() {
        searchService = new SearchServiceImpl(elasticsearchOperations, restHighLevelClient);
        ReflectionTestUtils.setField(searchService, "indexName", INDEX_NAME);
    }

    @Test
    void testSearch() {
        // Setup test data
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("test");
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        SearchableDocument document = new SearchableDocument();
        document.setId("1");
        document.setTitle("Test Document");

        List<SearchHit<SearchableDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHit);

        // Setup mocks
        when(searchHit.getContent()).thenReturn(document);
        when(searchHits.getSearchHits()).thenReturn(hitsList);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(SearchableDocument.class), any(IndexCoordinates.class)))
                .thenReturn(searchHits);

        // Execute test
        SearchResponse<SearchableDocument> response = searchService.search(searchRequest);

        // Verify results
        assertNotNull(response, "Response should not be null");
        assertEquals(1, response.getItems().size(), "Should return 1 item");
        assertEquals(document, response.getItems().get(0), "Should return correct document");
        assertEquals(1L, response.getTotalHits(), "Should return correct total hits count");
        assertEquals(0, response.getPage(), "Should return correct page number");
        assertEquals(10, response.getSize(), "Should return correct page size");
        assertTrue(response.getTook() >= 0, "Took time should be non-negative");

        // Verify interactions
        verify(elasticsearchOperations).search(any(CriteriaQuery.class), eq(SearchableDocument.class), any(IndexCoordinates.class));
    }

    @Test
    void testSearchWithException() {
        // Setup test data
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("test");

        // Setup mocks
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(SearchableDocument.class), any(IndexCoordinates.class)))
                .thenThrow(new RuntimeException("Test exception"));

        // Execute test & verify exception
        Exception exception = assertThrows(ElasticsearchQueryException.class, () -> {
            searchService.search(searchRequest);
        });

        assertTrue(exception.getMessage().contains("Failed to execute search query"), "Should throw with correct message");

        // Verify interactions
        verify(elasticsearchOperations).search(any(CriteriaQuery.class), eq(SearchableDocument.class), any(IndexCoordinates.class));
    }

    @Test
    void testCheckHealthWithExistingIndex() throws IOException {
        // Setup mocks
        when(restHighLevelClient.indices().exists(any(GetIndexRequest.class), eq(RequestOptions.DEFAULT)))
                .thenReturn(true);

        // Execute test
        String result = searchService.checkHealth();

        // Verify results
        assertTrue(result.startsWith("OK:"), "Should start with OK");
        assertTrue(result.contains(INDEX_NAME), "Should mention index name");

        // Verify interactions
        verify(restHighLevelClient.indices()).exists(any(GetIndexRequest.class), eq(RequestOptions.DEFAULT));
    }

    @Test
    void testCheckHealthWithMissingIndex() throws IOException {
        // Setup mocks
        when(restHighLevelClient.indices().exists(any(GetIndexRequest.class), eq(RequestOptions.DEFAULT)))
                .thenReturn(false);

        // Execute test
        String result = searchService.checkHealth();

        // Verify results
        assertTrue(result.startsWith("WARNING:"), "Should start with WARNING");
        assertTrue(result.contains(INDEX_NAME), "Should mention index name");

        // Verify interactions
        verify(restHighLevelClient.indices()).exists(any(GetIndexRequest.class), eq(RequestOptions.DEFAULT));
    }

    @Test
    void testCheckHealthWithException() throws IOException {
        // Setup mocks
        when(restHighLevelClient.indices().exists(any(GetIndexRequest.class), eq(RequestOptions.DEFAULT)))
                .thenThrow(new IOException("Test IO error"));

        // Execute test
        String result = searchService.checkHealth();

        // Verify results
        assertTrue(result.startsWith("ERROR:"), "Should start with ERROR");
        assertTrue(result.contains("Test IO error"), "Should contain error message");

        // Verify interactions
        verify(restHighLevelClient.indices()).exists(any(GetIndexRequest.class), eq(RequestOptions.DEFAULT));
    }
}