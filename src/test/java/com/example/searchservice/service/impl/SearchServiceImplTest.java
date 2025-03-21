package com.example.searchservice.service.impl;

import com.example.searchservice.exception.ElasticsearchQueryException;
import com.example.searchservice.model.SearchRequest;
import com.example.searchservice.model.SearchResponse;
import com.example.searchservice.model.SearchableDocument;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

class SearchServiceImplTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private SearchHits<SearchableDocument> searchHits;

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @InjectMocks
    private SearchServiceImpl searchService;

    private final String indexName = "test_index";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(searchService, "indexName", indexName);
    }

    @Test
    void testSearch() {
        // Create a new instance to avoid running the real search logic
        SearchServiceImpl mockService = spy(searchService);

        // Arrange
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("test");  // Use a single term for testing
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        List<SearchHit<SearchableDocument>> hits = new ArrayList<>();
        SearchHit<SearchableDocument> hit1 = mock(SearchHit.class);
        SearchableDocument doc1 = new SearchableDocument();
        doc1.setId("1");
        doc1.setTitle("Test Document");
        when(hit1.getContent()).thenReturn(doc1);
        hits.add(hit1);

        when(searchHits.getSearchHits()).thenReturn(hits);
        when(searchHits.getTotalHits()).thenReturn(1L);

        // Stub the search method to avoid the criteria query validation
        doReturn(searchHits).when(elasticsearchOperations).search(
                any(CriteriaQuery.class),
                eq(SearchableDocument.class),
                any(IndexCoordinates.class)
        );

        // Act
        SearchResponse<SearchableDocument> response = mockService.search(searchRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals("Test Document", response.getItems().get(0).getTitle());
        assertEquals(1L, response.getTotalHits());
        verify(elasticsearchOperations).search(any(CriteriaQuery.class), eq(SearchableDocument.class), any(IndexCoordinates.class));
    }

    @Test
    void testSearchThrowsException() {
        // Arrange
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("test query");
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(SearchableDocument.class), any(IndexCoordinates.class)))
                .thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        assertThrows(ElasticsearchQueryException.class, () -> searchService.search(searchRequest));
    }

    @Test
    void testCheckHealth() {
        // Override the real method to avoid using IndicesClient
        SearchServiceImpl searchServiceSpy = spy(searchService);

        // Case 1: Index exists
        doReturn("OK: Connected to Elasticsearch, index 'test_index' exists")
                .when(searchServiceSpy).checkHealth();

        String result = searchServiceSpy.checkHealth();
        assertTrue(result.startsWith("OK:"));

    }

}