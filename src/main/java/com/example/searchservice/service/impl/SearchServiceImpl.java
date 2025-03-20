package com.example.searchservice.service.impl;

import com.example.searchservice.exception.ElasticsearchQueryException;
import com.example.searchservice.model.SearchRequest;
import com.example.searchservice.model.SearchResponse;
import com.example.searchservice.model.SearchableDocument;
import com.example.searchservice.service.SearchService;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final ElasticsearchOperations elasticsearchOperations;
    private final RestHighLevelClient restHighLevelClient;

    @Value("${elasticsearch.index.name}")
    private String indexName;

    @Autowired
    public SearchServiceImpl(ElasticsearchOperations elasticsearchOperations,
                             RestHighLevelClient restHighLevelClient) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * Performs a basic search using the provided search request.
     *
     * @param searchRequest The search request containing query parameters
     * @return A search response with results and metadata
     */
    @Override
    public SearchResponse<SearchableDocument> search(SearchRequest searchRequest) {
        logger.info("Performing search with query: {}", searchRequest.getQuery());

        long startTime = System.currentTimeMillis();

        try {
            // Create a simple criteria query
            Criteria criteria = new Criteria("title").contains(searchRequest.getQuery())
                    .or("content").contains(searchRequest.getQuery());

            CriteriaQuery query = new CriteriaQuery(criteria);
            query.setPageable(PageRequest.of(searchRequest.getPage(), searchRequest.getSize()));

            // Execute the search
            SearchHits<SearchableDocument> searchHits = elasticsearchOperations.search(
                    query,
                    SearchableDocument.class,
                    IndexCoordinates.of(indexName)
            );

            // Extract results
            List<SearchableDocument> documents = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());

            long took = System.currentTimeMillis() - startTime;

            // Create response
            return new SearchResponse<>(
                    documents,
                    searchHits.getTotalHits(),
                    searchRequest.getPage(),
                    searchRequest.getSize(),
                    null,
                    took
            );
        } catch (Exception e) {
            logger.error("Error during search: {}", e.getMessage(), e);
            throw new ElasticsearchQueryException("Failed to execute search query", e);
        }
    }

    /**
     * Checks the health of the Elasticsearch connection.
     *
     * @return A status message indicating the health of the connection
     */
    @Override
    public String checkHealth() {
        try {
            boolean indexExists = restHighLevelClient.indices()
                    .exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);

            if (!indexExists) {
                return "WARNING: Index '" + indexName + "' does not exist";
            }

            return "OK: Connected to Elasticsearch, index '" + indexName + "' exists";
        } catch (IOException e) {
            logger.error("Error checking Elasticsearch health: {}", e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }
    }

}