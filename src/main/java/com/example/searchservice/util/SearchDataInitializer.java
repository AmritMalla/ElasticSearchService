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

@Component
@Profile({"dev", "test"}) // Only run in development and test environments
public class SearchDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SearchDataInitializer.class);
    
    private final SearchRepository searchRepository;
    
    @Value("${elasticsearch.data.initialize:false}")
    private boolean shouldInitializeData;
    
    @Value("${elasticsearch.data.count:50}")
    private int documentCount;

    @Autowired
    public SearchDataInitializer(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    /**
     * Initialize the Elasticsearch index with sample data when the application starts.
     * This method is triggered after the application is ready to receive requests.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadSampleData() {
        if (!shouldInitializeData) {
            logger.info("Sample data initialization is disabled");
            return;
        }
        
        logger.info("Initializing Elasticsearch with {} sample documents", documentCount);
        
        try {
            // Count existing documents
            long existingCount = searchRepository.count();
            
            if (existingCount > 0) {
                logger.info("Elasticsearch already contains {} documents, skipping initialization", existingCount);
                return;
            }
            
            // Generate random documents
            List<SearchableDocument> documents = SearchDataGenerator.generateRandomDocuments(documentCount);
            
            // Save all documents
            searchRepository.saveAll(documents);
            
            logger.info("Successfully initialized Elasticsearch with {} sample documents", documentCount);
        } catch (Exception e) {
            logger.error("Failed to initialize Elasticsearch with sample data", e);
        }
    }
}