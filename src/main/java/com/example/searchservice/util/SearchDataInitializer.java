package com.example.searchservice.util;

import com.example.searchservice.model.SearchableDocument;
import com.example.searchservice.repository.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initializes the Elasticsearch index with sample data on application startup.
 * This component is active only in "dev" and "test" profiles.
 */
@Component
@Profile({"dev", "test"}) // Only run in development and test environments
public class SearchDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SearchDataInitializer.class);

    private final SearchRepository searchRepository;

    @Value("${elasticsearch.data.initialize:false}")
    private boolean shouldInitializeData;  // Controls whether initialization should occur

    @Value("${elasticsearch.data.count:50}")
    private int documentCount;  // Number of sample documents to generate

    /**
     * Constructs a new SearchDataInitializer with the required repository dependency.
     *
     * @param searchRepository the repository for managing SearchableDocument entities
     */
    @Autowired
    public SearchDataInitializer(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    /**
     * Initializes the Elasticsearch index with sample data when the application is fully started.
     * This method runs only if data initialization is enabled and no documents exist in the index.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadSampleData() {
        // Check if initialization is disabled via configuration
        if (!shouldInitializeData) {
            logger.info("Sample data initialization is disabled");
            return;
        }

        logger.info("Initializing Elasticsearch with {} sample documents", documentCount);

        try {
            // Check current document count in the index
            long existingCount = searchRepository.count();

            // Skip initialization if data already exists
            if (existingCount > 0) {
                logger.info("Elasticsearch already contains {} documents, skipping initialization", existingCount);
                return;
            }

            // Generate sample documents using the data generator
            List<SearchableDocument> documents = SearchDataGenerator.generateRandomDocuments(documentCount);

            // Persist all generated documents to Elasticsearch
            searchRepository.saveAll(documents);

            logger.info("Successfully initialized Elasticsearch with {} sample documents", documentCount);
        } catch (Exception e) {
            logger.error("Failed to initialize Elasticsearch with sample data", e);
        }
    }
}