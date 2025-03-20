package com.example.searchservice.repository;

import com.example.searchservice.model.SearchableDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends ElasticsearchRepository<SearchableDocument, String> {

}
