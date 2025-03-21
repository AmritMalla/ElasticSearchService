package com.example.searchservice.controller;

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
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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

@ExtendWith(SpringExtension.class)
@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SearchService searchService;

    @MockBean
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    void testHealthCheck() throws Exception {
        // Setup mock
        when(searchService.checkHealth()).thenReturn("OK: Connected to Elasticsearch");

        // Execute and verify
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OK: Connected to Elasticsearch")));

        verify(searchService).checkHealth();
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void testSearchWithValidRequest() throws Exception {
        // Setup request
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("test");
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        // Setup mocks
        List<SearchableDocument> documents = new ArrayList<>();
        SearchableDocument document = new SearchableDocument();
        document.setId("1");
        document.setTitle("Test Document");
        documents.add(document);

        SearchResponse<SearchableDocument> response = new SearchResponse<>(
                documents, 1L, 0, 10, null, 42L
        );

        when(searchService.search(any(SearchRequest.class))).thenReturn(response);

        // Execute and verify
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

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void testSearchWithInvalidRequest() throws Exception {
        // Setup invalid request (missing required query field)
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        // Execute and verify validation error
        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Validation error")));

        // Verify service was not called
        verify(searchService, never()).search(any(SearchRequest.class));
    }

    @Test
    void testSearchWithoutAuthentication() throws Exception {
        // Setup request
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("test");

        // Execute and verify authentication required
        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isUnauthorized());

        // Verify service was not called
        verify(searchService, never()).search(any(SearchRequest.class));
    }
}