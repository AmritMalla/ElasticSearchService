## Folder Structure
```plaintext
ElasticSearchService/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── searchservice/
│   │   │               ├── ElasticSearchServiceApplication.java
│   │   │               ├── config/
│   │   │               │   ├── ElasticsearchConfig.java
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── controller/
│   │   │               │   └── SearchController.java
│   │   │               ├── exception/
│   │   │               │   ├── ElasticsearchQueryException.java
│   │   │               │   └── GlobalExceptionHandler.java
│   │   │               ├── model/
│   │   │               │   ├── ErrorResponse.java
│   │   │               │   ├── SearchRequest.java
│   │   │               │   ├── SearchResponse.java
│   │   │               │   └── SearchableDocument.java
│   │   │               ├── repository/
│   │   │               │   └── SearchRepository.java
│   │   │               ├── service/
│   │   │               │   ├── SearchService.java
│   │   │               │   └── impl/
│   │   │               │       └── SearchServiceImpl.java
│   │   │               └── util/
│   │   │                   ├── SearchDataGenerator.java
│   │   │                   └── SearchDataInitializer.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── elasticsearch-settings.json
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── searchservice/
│                       ├── ElasticSearchServiceApplicationTest.java
│                       ├── config/
│                       │   ├── ElasticsearchConfigTest.java
│                       │   ├── SecurityConfigTest.java
│                       │   └── TestConfig.java
│                       ├── controller/
│                       │   └── SearchControllerTest.java
│                       ├── exception/
│                       │   ├── ElasticsearchQueryExceptionTest.java
│                       │   └── GlobalExceptionHandlerTest.java
│                       ├── model/
│                       │   ├── ErrorResponseTest.java
│                       │   ├── SearchRequestTest.java
│                       │   ├── SearchResponseTest.java
│                       │   └── SearchableDocumentTest.java
│                       ├── service/
│                       │   └── impl/
│                       │       └── SearchServiceImplTest.java
│                       └── util/
│                           ├── SearchDataGeneratorTest.java
│                           └── SearchDataInitializerTest.java
├── build.gradle
└── settings.gradle

```

## 1. ElasticSearchServiceApplication: `src/main/java/com/example/searchservice/ElasticSearchServiceApplication.java`
```java
package com.example.searchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Main application class for the Elasticsearch-based search service.
 * This class bootstraps the Spring Boot application and enables Elasticsearch repository support.
 */
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.example.searchservice.repository")
public class ElasticSearchServiceApplication {

	/**
	 * Entry point for the Spring Boot application.
	 * Initializes and runs the application with the provided command-line arguments.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(ElasticSearchServiceApplication.class, args);  // Start the Spring Boot application
	}
}

```

## 2. SearchController: `src/main/java/com/example/searchservice/controller/SearchController.java`
```java
package com.example.searchservice.controller;

import com.example.searchservice.model.SearchRequest;
import com.example.searchservice.model.SearchResponse;
import com.example.searchservice.model.SearchableDocument;
import com.example.searchservice.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller that handles search-related API endpoints.
 * Provides basic search functionality and service health checks.
 */
@RestController
@RequestMapping("/api")
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    /**
     * Search service to handle search operations.
     */
    private final SearchService searchService;

    /**
     * Constructor for dependency injection.
     *
     * @param searchService Service for search operations
     */
    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * GET /api/health
     *
     * Health check endpoint to verify the service and Elasticsearch are operational.
     *
     * @return Health status string wrapped in ResponseEntity
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check requested");

        // Delegate to the service to get Elasticsearch health
        String health = searchService.checkHealth();

        return ResponseEntity.ok(health);
    }

    /**
     * POST /api/search
     *
     * Endpoint for executing a search query against the Elasticsearch index.
     *
     * @param searchRequest Validated search query and pagination info
     * @return Search response containing a list of matching documents and metadata
     */
    @PostMapping("/search")
    public ResponseEntity<SearchResponse<SearchableDocument>> search(
            @Valid @RequestBody SearchRequest searchRequest) {

        // Log the incoming query
        logger.info("Search requested with query: {}", searchRequest.getQuery());

        // Record start time for performance logging
        long startTime = System.currentTimeMillis();

        // Perform the search operation
        SearchResponse<SearchableDocument> response = searchService.search(searchRequest);

        // Log duration and result count
        logger.info("Search completed in {} ms with {} results",
                System.currentTimeMillis() - startTime, response.getTotalHits());

        return ResponseEntity.ok(response);
    }
}

```
## 3. ElasticsearchQueryException: `src/main/java/com/example/searchservice/exception/ElasticsearchQueryException.java`
```java
package com.example.searchservice.exception;

/**
 * Exception thrown when an Elasticsearch query fails to execute properly.
 * This is a runtime exception indicating an issue with query processing or connectivity.
 */
public class ElasticsearchQueryException extends RuntimeException {

    /**
     * Constructs a new ElasticsearchQueryException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the underlying cause of the exception (e.g., an IOException or Elasticsearch-specific error)
     */
    public ElasticsearchQueryException(String message, Throwable cause) {
        super(message, cause);  // Pass message and cause to RuntimeException constructor
    }
}
```
## 4. GlobalExceptionHandler: `src/main/java/com/example/searchservice/exception/GlobalExceptionHandler.java`
```java
package com.example.searchservice.exception;

import com.example.searchservice.model.ErrorResponse;
import org.elasticsearch.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ElasticsearchQueryException and returns a 500 Internal Server Error response.
     *
     * @param ex ElasticsearchQueryException thrown by the application
     * @param request Current HTTP request
     * @return ResponseEntity with error information
     */
    @ExceptionHandler(ElasticsearchQueryException.class)
    public ResponseEntity<ErrorResponse> handleElasticsearchQueryException(
            ElasticsearchQueryException ex, HttpServletRequest request) {
        
        logger.error("Elasticsearch query error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error executing search query: " + ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles ElasticsearchException and returns a 500 Internal Server Error response.
     *
     * @param ex ElasticsearchException thrown by Elasticsearch client
     * @param request Current HTTP request
     * @return ResponseEntity with error information
     */
    @ExceptionHandler(ElasticsearchException.class)
    public ResponseEntity<ErrorResponse> handleElasticsearchException(
            ElasticsearchException ex, HttpServletRequest request) {
        
        logger.error("Elasticsearch error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Elasticsearch error: " + ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles validation errors and returns a 400 Bad Request response.
     *
     * @param ex MethodArgumentNotValidException thrown during request validation
     * @param request Current HTTP request
     * @return ResponseEntity with detailed validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        logger.error("Validation error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                request.getRequestURI()
        );
        
        // Add individual field errors to the response
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorResponse.addValidationError(error.getField(), error.getDefaultMessage());
        });
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback handler for all other exceptions.
     *
     * @param ex Exception thrown by the application
     * @param request Current HTTP request
     * @return ResponseEntity with error information
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
## 5. ErrorResponse: `src/main/java/com/example/searchservice/model/ErrorResponse.java`
```java
package com.example.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an error response containing details about an error that occurred during request processing.
 */
@Getter
@Setter
public class ErrorResponse {

    /**
     * Timestamp when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code associated with the error.
     */
    private int status;

    /**
     * Descriptive message about the error.
     */
    private String message;

    /**
     * URL path of the request that caused the error.
     */
    private String path;

    /**
     * List of validation errors associated with the request.
     */
    private List<ValidationError> errors = new ArrayList<>();

    /**
     * Constructs a new ErrorResponse with the specified status, message, and path.
     *
     * @param status the HTTP status code
     * @param message the error message
     * @param path the request path that triggered the error
     */
    public ErrorResponse(int status, String message, String path) {
        this.timestamp = LocalDateTime.now();  // Set current timestamp when error occurs
        this.status = status;
        this.message = message;
        this.path = path;
    }

    /**
     * Adds a validation error to the list of errors.
     *
     * @param field the field that failed validation
     * @param message the validation error message
     */
    public void addValidationError(String field, String message) {
        this.errors.add(new ValidationError(field, message));  // Append new validation error to the list
    }

    /**
     * Represents a single validation error with its field and message.
     */
    @Getter
    @AllArgsConstructor
    public static class ValidationError {
        /**
         * The field that failed validation.
         */
        private String field;

        /**
         * The validation error message.
         */
        private String message;
    }
}
```

## 6. SearchRequest: `src/main/java/com/example/searchservice/model/SearchRequest.java`
```java
package com.example.searchservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * Represents a search request with query parameters and filtering options.
 */
@Getter
@Setter
public class SearchRequest {

    /**
     * The main search query string.
     */
    @NotBlank(message = "Search query is required")
    @Size(min = 2, message = "Search query must be at least 2 characters")
    private String query;

    /**
     * List of fields to search within. If empty or null, search will be performed across all available fields.
     */
    private List<String> fields;

    /**
     * Current page number for pagination, starting from 0.
     */
    @Min(value = 0, message = "Page number must be non-negative")
    private int page = 0;  // Default to first page

    /**
     * Number of results to return per page.
     */
    @Min(value = 1, message = "Page size must be positive")
    private int size = 10;  // Default to 10 results per page

    /**
     * Map specifying fields to sort by and their sort directions ("asc" or "desc").
     */
    private Map<String, String> sort;

    /**
     * Map of field names to filter values for exact matching.
     */
    private Map<String, Object> filters;

    /**
     * Start date for date range filtering in ISO format (e.g., "2023-01-01").
     */
    private String dateFrom;

    /**
     * End date for date range filtering in ISO format (e.g., "2023-12-31").
     */
    private String dateTo;

    /**
     * Field name to apply the date range filter to.
     */
    private String dateField = "createdDate";  // Default date field for filtering

    /**
     * Minimum relevance score for results to be included in the response.
     */
    private Float minScore;
}
```

## 7. SearchResponse: `src/main/java/com/example/searchservice/model/SearchResponse.java`
```java
package com.example.searchservice.model;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class SearchResponse<T> {

    /**
     * List of search result items.
     */
    private List<T> items;

    /**
     * Total number of matching results.
     */
    private long totalHits;

    /**
     * Current page number.
     */
    private int page;

    /**
     * Number of results per page.
     */
    private int size;

    /**
     * Total number of available pages.
     */
    private int totalPages;

    /**
     * Flag indicating if there are more pages available.
     */
    private boolean hasNext;

    /**
     * Aggregated information from the search results.
     */
    private Map<String, Map<String, Long>> aggregations;

    /**
     * Time taken to execute the search in milliseconds.
     */
    private long took;

    /**
     * Constructor for creating a search response with results and metadata.
     *
     * @param items        List of search result items
     * @param totalHits    Total number of matching results
     * @param page         Current page number
     * @param size         Number of results per page
     * @param aggregations Map of aggregation results
     * @param took         Time taken to execute the search
     */
    public SearchResponse(List<T> items, long totalHits, int page, int size,
                          Map<String, Map<String, Long>> aggregations, long took) {
        this.items = items;
        this.totalHits = totalHits;
        this.page = page;
        this.size = size;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalHits / size) : 0;
        this.hasNext = page + 1 < this.totalPages;
        this.aggregations = aggregations;
        this.took = took;
    }

}
```

## 8. SearchableDocument: `src/main/java/com/example/searchservice/model/SearchableDocument.java`
```java
package com.example.searchservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;
import java.util.Map;

@Document(indexName = "#{@environment.getProperty('elasticsearch.index.name')}")
@Setting(settingPath = "elasticsearch-settings.json")
@Getter
@Setter
public class SearchableDocument {

    /**
     * Unique identifier for the document.
     */
    @Id
    private String id;

    /**
     * Title of the document with text analysis for better search.
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    /**
     * Content of the document with text analysis for better search.
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    /**
     * Author of the document.
     */
    @Field(type = FieldType.Keyword)
    private String author;

    /**
     * Date when the document was created.
     */
    @Field(type = FieldType.Date)
    private Date createdDate;

    /**
     * Date when the document was last updated.
     */
    @Field(type = FieldType.Date)
    private Date lastUpdatedDate;

    /**
     * Category or type of the document.
     */
    @Field(type = FieldType.Keyword)
    private String category;

    /**
     * Tags associated with the document for faceted search.
     */
    @Field(type = FieldType.Keyword)
    private String[] tags;

    /**
     * Dynamic fields that can store additional document properties.
     */
    @Field(type = FieldType.Object)
    private Map<String, Object> metadata;

}

```

## 9. SearchRepository: `src/main/java/com/example/searchservice/repository/SearchRepository.java`
```java
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
```

## 10. SearchService: `src/main/java/com/example/searchservice/service/SearchService.java`
```java
package com.example.searchservice.service;

import com.example.searchservice.model.SearchRequest;
import com.example.searchservice.model.SearchResponse;
import com.example.searchservice.model.SearchableDocument;

/**
 * Service interface defining search operations and health checks for the search system.
 */
public interface SearchService {

    /**
     * Performs a search operation based on the provided request parameters.
     *
     * @param searchRequest the search request containing query parameters and filters
     * @return a SearchResponse containing the search results and metadata
     */
    SearchResponse<SearchableDocument> search(SearchRequest searchRequest);

    /**
     * Checks the health status of the Elasticsearch cluster.
     *
     * @return a string representing the cluster health status (e.g., "green", "yellow", "red")
     */
    String checkHealth();
}
```

## 11. SearchServiceImpl: `src/main/java/com/example/searchservice/service/impl/SearchServiceImpl.java`
```java
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
            // Create separate criteria for each field and combine them
            Criteria titleCriteria = new Criteria("title").matches(searchRequest.getQuery());
            Criteria contentCriteria = new Criteria("content").matches(searchRequest.getQuery());

            // Combine with OR
            Criteria combinedCriteria = titleCriteria.or(contentCriteria);

            // Create and configure the query
            CriteriaQuery query = new CriteriaQuery(combinedCriteria);
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
```

## 12. SearchDataGenerator: `src/main/java/com/example/searchservice/util/SearchDataGenerator.java`
```java
/**
 * Utility class to generate dummy search document data.
 * This can be used for testing, development, or initial data loading.
 */
package com.example.searchservice.util;

import com.example.searchservice.model.SearchableDocument;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Generates random SearchableDocument instances for testing and development purposes.
 */
public class SearchDataGenerator {

    private static final Random random = new Random();

    // Categories for documents
    private static final String[] CATEGORIES = {
            "Technology", "Science", "Business", "Finance", "Health",
            "Sports", "Entertainment", "Politics", "Education", "Environment"
    };

    // Authors for documents
    private static final String[] AUTHORS = {
            "John Smith", "Emily Johnson", "Michael Brown", "Sarah Lee",
            "David Wilson", "Jennifer Garcia", "Robert Martinez", "Linda Anderson",
            "William Taylor", "Elizabeth Thomas"
    };

    // Tags for documents
    private static final String[] TAGS = {
            "research", "report", "analysis", "guide", "tutorial",
            "review", "summary", "whitepaper", "case-study", "reference",
            "latest", "trending", "featured", "popular", "recommended"
    };

    // Sample titles with placeholder for category
    private static final String[] TITLE_TEMPLATES = {
            "Comprehensive Guide to %s",
            "How to Master %s in 2025",
            "The Ultimate %s Handbook",
            "Understanding %s: A Deep Dive",
            "%s Fundamentals Explained",
            "Essential %s Strategies",
            "The Future of %s",
            "%s Best Practices",
            "Advanced %s Techniques",
            "%s: Trends and Insights"
    };

    // Sample content segments for building document content
    private static final String[] CONTENT_SEGMENTS = {
            "This comprehensive document explores the fundamental aspects of the subject matter. ",
            "In recent years, significant developments have transformed this field. ",
            "Experts agree that the most critical factor to consider is thorough research. ",
            "According to the latest studies, the trend is likely to continue into the next decade. ",
            "Several case studies demonstrate the effectiveness of this approach. ",
            "The data indicates a strong correlation between these variables. ",
            "Best practices suggest implementing a structured methodology. ",
            "Analysis of key metrics reveals important insights about performance. ",
            "A comparative assessment of different techniques shows varying results. ",
            "Future developments will likely focus on improving efficiency and scalability. ",
            "Integration with existing systems remains a significant challenge. ",
            "Stakeholders should consider multiple factors before making decisions. ",
            "The implementation strategy should account for potential risks. ",
            "Continuous monitoring and evaluation are essential for success. ",
            "Feedback from users has been incorporated into the recommendations. "
    };

    /**
     * Generates a specified number of random searchable documents.
     *
     * @param count the number of documents to generate
     * @return a list of randomly generated SearchableDocument instances
     */
    public static List<SearchableDocument> generateRandomDocuments(int count) {
        List<SearchableDocument> documents = new ArrayList<>(count);  // Pre-allocate list with expected size

        // Generate the requested number of documents
        for (int i = 0; i < count; i++) {
            documents.add(createRandomDocument());
        }

        return documents;
    }

    /**
     * Creates a single random document with realistic attributes.
     *
     * @return a newly generated SearchableDocument instance
     */
    public static SearchableDocument createRandomDocument() {
        SearchableDocument document = new SearchableDocument();

        // Assign a unique identifier
        document.setId(UUID.randomUUID().toString());

        // Select a random category
        String category = CATEGORIES[random.nextInt(CATEGORIES.length)];
        document.setCategory(category);

        // Generate a title using a template and the selected category
        String titleTemplate = TITLE_TEMPLATES[random.nextInt(TITLE_TEMPLATES.length)];
        document.setTitle(String.format(titleTemplate, category));

        // Generate random content with varying paragraph count
        document.setContent(generateRandomContent(3 + random.nextInt(5)));

        // Assign a random author
        document.setAuthor(AUTHORS[random.nextInt(AUTHORS.length)]);

        // Assign 2-4 random unique tags
        document.setTags(generateRandomTags(2 + random.nextInt(3)));

        // Generate a creation date within the last 2 years
        Date createdDate = generateRandomDate(365 * 2);
        document.setCreatedDate(createdDate);

        // Generate a last updated date between creation and now
        long createdTime = createdDate.getTime();
        long now = System.currentTimeMillis();
        long updateTime = createdTime + Math.abs(random.nextLong() % (now - createdTime));  // Ensure update is after creation
        document.setLastUpdatedDate(new Date(updateTime));

        return document;
    }

    /**
     * Generates random content by combining content segments into paragraphs.
     *
     * @param paragraphs the number of paragraphs to generate
     * @return a string containing the generated content
     */
    private static String generateRandomContent(int paragraphs) {
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < paragraphs; i++) {
            // Generate 3-6 sentences per paragraph
            int sentences = 3 + random.nextInt(4);

            for (int j = 0; j < sentences; j++) {
                content.append(CONTENT_SEGMENTS[random.nextInt(CONTENT_SEGMENTS.length)]);
            }

            content.append("\n\n");  // Separate paragraphs with double newline
        }

        return content.toString().trim();  // Remove trailing whitespace
    }

    /**
     * Generates an array of unique random tags.
     *
     * @param count the number of tags to generate
     * @return an array of randomly selected unique tags
     */
    private static String[] generateRandomTags(int count) {
        String[] result = new String[count];
        List<Integer> usedIndices = new ArrayList<>();  // Track used indices to ensure uniqueness

        for (int i = 0; i < count; i++) {
            int index;
            do {
                index = random.nextInt(TAGS.length);
            } while (usedIndices.contains(index));  // Repeat until we find an unused tag

            usedIndices.add(index);
            result[i] = TAGS[index];
        }

        return result;
    }

    /**
     * Generates a random date within a specified number of days before the current time.
     *
     * @param maxDaysAgo the maximum number of days in the past for the generated date
     * @return a randomly generated Date object
     */
    private static Date generateRandomDate(int maxDaysAgo) {
        long now = System.currentTimeMillis();
        long randomTime = now - TimeUnit.DAYS.toMillis(random.nextInt(maxDaysAgo));
        return new Date(randomTime);
    }
}
```

## 13. SearchDataInitializer: `src/main/java/com/example/searchservice/util/SearchDataInitializer.java`
```java
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
```

## 14. ElasticsearchConfig: `src/main/java/com/example/searchservice/config/ElasticsearchConfig.java`
```java
package com.example.searchservice.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.time.Duration;

/**
 * Configuration class for setting up Elasticsearch client and operations in a Spring Boot application.
 * <p>
 * This class defines beans for {@link RestHighLevelClient} and {@link ElasticsearchOperations},
 * allowing interaction with an Elasticsearch cluster. Connection details and timeouts are loaded
 * from the application's properties.
 */
@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    /**
     * Host URL for the Elasticsearch cluster. Injected from application properties using the key {@code elasticsearch.host}.
     */
    @Value("${elasticsearch.host}")
    private String host;

    /**
     * Connection timeout in milliseconds. Defaults to 5000 ms if not specified.
     * Injected from application properties using the key {@code elasticsearch.connection.timeout}.
     */
    @Value("${elasticsearch.connection.timeout:5000}")
    private int connectionTimeout;

    /**
     * Socket timeout in milliseconds. Defaults to 10000 ms if not specified.
     * Injected from application properties using the key {@code elasticsearch.socket.timeout}.
     */
    @Value("${elasticsearch.socket.timeout:10000}")
    private int socketTimeout;

    /**
     * Creates and configures a {@link RestHighLevelClient} for connecting to the Elasticsearch cluster.
     *
     * <p>This client is used by Spring Data Elasticsearch to perform low-level operations.</p>
     *
     * @return a configured {@link RestHighLevelClient} instance.
     */
    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        // Build client configuration with host and timeout settings
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(host)  // Elasticsearch host, e.g., "localhost:9200"
                .withConnectTimeout(Duration.ofMillis(connectionTimeout)) // Time allowed to establish the connection
                .withSocketTimeout(Duration.ofMillis(socketTimeout)) // Time allowed to wait for data on socket
                .build();

        // Create and return a RestHighLevelClient from the configuration
        return RestClients.create(clientConfiguration).rest();
    }

    /**
     * Creates a bean of {@link ElasticsearchOperations} using the configured client.
     *
     * <p>This template provides high-level operations for indexing, querying, and managing documents in Elasticsearch.</p>
     *
     * @return an instance of {@link ElasticsearchOperations}.
     */
    @Bean
    public ElasticsearchOperations elasticsearchOperations() {
        // Use RestHighLevelClient to create a higher-level template API
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}

```

## 15. SecurityConfig: `src/main/java/com/example/searchservice/config/SecurityConfig.java`
```java
package com.example.searchservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 *
 * <p>Defines the security rules and authentication mechanisms, including in-memory users
 * and password encoding. Uses HTTP Basic and form-based authentication.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * Sets up authorization rules, CSRF settings, and authentication mechanisms.
     *
     * @param http HttpSecurity to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection for stateless API calls
                .authorizeRequests()
                .antMatchers("/api/health").permitAll() // Allow unauthenticated access to health check endpoint
                .antMatchers("/api/search/**").authenticated() // Require authentication for search-related endpoints
                .anyRequest().authenticated() // All other endpoints require authentication
                .and()
                .httpBasic() // Enable HTTP Basic Authentication
                .and()
                .formLogin(); // Enable form-based login for UI access

        return http.build(); // Build and return the configured security filter chain
    }

    /**
     * Defines an in-memory user details service for testing/demo purposes.
     * In production, consider integrating with a persistent user store (e.g., database, LDAP).
     *
     * @return InMemoryUserDetailsManager with predefined users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Define user with role USER
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password")) // Encode password using BCrypt
                .roles("USER")
                .build();

        // Define admin with roles USER and ADMIN
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin")) // Encode password using BCrypt
                .roles("USER", "ADMIN")
                .build();

        // Create an in-memory user store with the defined users
        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * Creates and configures a password encoder using BCrypt.
     * Used to hash and validate user passwords securely.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for strong password hashing
    }
}

```

