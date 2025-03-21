package com.example.searchservice.controller;

import com.example.searchservice.config.SecurityConfig;
import com.example.searchservice.config.TestConfig;
import com.example.searchservice.exception.GlobalExceptionHandler;
import com.example.searchservice.model.SearchRequest;
import com.example.searchservice.model.SearchResponse;
import com.example.searchservice.model.SearchableDocument;
import com.example.searchservice.service.SearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for SearchController using WebMvcTest and MockMvc.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = SearchController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = EnableElasticsearchRepositories.class
        )
)
@Import({TestConfig.class, SecurityConfig.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration"
})
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SearchService searchService;

    /**
     * Verifies that the health endpoint returns a valid health status message.
     */
    @Test
    void testHealthCheck() throws Exception {
        // Mock the service response
        when(searchService.checkHealth()).thenReturn("OK: Connected to Elasticsearch");

        // Perform GET request and assert expected status and content
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OK: Connected to Elasticsearch")));

        // Verify service method was called
        verify(searchService).checkHealth();
    }

    /**
     * Validates that a properly authenticated and well-formed search request returns expected results.
     */
    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void testSearchWithValidRequest() throws Exception {
        // Build request
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("test");
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        // Mock service response
        List<SearchableDocument> documents = new ArrayList<>();
        SearchableDocument document = new SearchableDocument();
        document.setId("1");
        document.setTitle("Test Document");
        documents.add(document);

        SearchResponse<SearchableDocument> response = new SearchResponse<>(
                documents, 1L, 0, 10, null, 42L
        );

        when(searchService.search(any(SearchRequest.class))).thenReturn(response);

        // Execute POST and validate response body
        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is("1")))
                .andExpect(jsonPath("$.items[0].title", is("Test Document")))
                .andExpect(jsonPath("$.totalHits", is(1)))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.took", is(42)));

        verify(searchService).search(any(SearchRequest.class));
    }

    /**
     * Verifies that an invalid request (missing required fields) returns HTTP 400
     * and does not invoke the service layer.
     */
    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void testSearchWithInvalidRequest() throws Exception {
        // Missing required 'query' field
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        // Perform request and expect validation failure
        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isBadRequest());

        // Verify search service is NOT called
        verify(searchService, never()).search(any(SearchRequest.class));
    }

    /**
     * Ensures that a search request made without authentication returns HTTP 401 Unauthorized.
     */
    @Test
    void testSearchWithoutAuthentication() throws Exception {
        // Create valid request but without authentication
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("test");

        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isUnauthorized());

        verify(searchService, never()).search(any(SearchRequest.class));
    }
}
