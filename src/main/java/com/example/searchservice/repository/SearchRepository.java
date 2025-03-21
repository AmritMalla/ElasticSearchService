package com.example.searchservice.repository;

import com.example.searchservice.model.SearchableDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing search operations on SearchableDocument entities using Elasticsearch.
 */
@Repository
public interface SearchRepository extends ElasticsearchRepository<SearchableDocument, String> {
    // No additional methods defined; inherits CRUD and search operations from ElasticsearchRepository
}