# Prompt Title
Spring Boot Elasticsearch Search Service with Document Indexing, Querying, and Health Monitoring

## High Level Description
Develop a robust Elasticsearch search service in Spring Boot to enable advanced document search capabilities. The service will use Elasticsearch as the search engine to:
* Index and search documents with customizable fields and metadata.
* Provide flexible search queries with filtering, sorting, and pagination.
* Support health monitoring of the Elasticsearch connection and indices.
  The service will expose REST endpoints for clients to interact with the search system.

## Functions / Classes to Be Created by LLM

1. **SearchableDocument.java**
* **Functionality:**
    * Document model with fields for metadata and content.
    * Uses Elasticsearch annotations for proper indexing.
* **Annotations:**
    * Uses `@Document` to configure index settings.
    * Uses `@Field` to define field types and analyzers.

2. **SearchRequest.java**
* **Functionality:**
    * Encapsulates search parameters including query, fields, pagination, and filters.
* **Annotations:**
    * Includes validation constraints (`@NotBlank`, `@Size`, `@Min`).
* Supports field-specific searching and date range filters.

3. **SearchResponse.java**
* **Functionality:**
    * Wraps search results with metadata like total hits and pagination details.
    * Calculates pagination metrics (totalPages, hasNext).
* Tracks search execution time.

4. **SearchService.java**
* **Functionality:**
    * `search(SearchRequest request)`: Executes search queries against Elasticsearch.
    * `checkHealth()`: Monitors Elasticsearch connection health.
* Handles Elasticsearch connection and query exceptions.

5. **SearchController.java**
* **REST API Endpoints:**
    * `POST /api/search`: Execute search with query parameters.
    * `GET /api/health`: Check Elasticsearch connection health.
* Validates request inputs.
* Logs all search operations.

6. **ElasticsearchConfig.java**
* Configures:
    * RestHighLevelClient with connection parameters.
    * ElasticsearchOperations for simplified operations.
    * Timeout settings for connection and socket operations.

7. **SecurityConfig.java**
* Configures:
    * Authentication for API endpoints.
    * Different security rules for search and health endpoints.
    * UserDetailsService for in-memory test users.

8. **GlobalExceptionHandler.java**
* Handles exceptions globally using `@ControllerAdvice`.
* Returns structured error responses with:
    * Elasticsearch-specific errors.
    * Validation errors with field details.
    * Generic server errors.

9. **SearchDataGenerator.java and SearchDataInitializer.java**
* Generates sample documents for testing.
* Initializes the Elasticsearch index with test data in development environments.

## Dependencies to Use
* `spring-boot-starter-web`
* `spring-boot-starter-data-elasticsearch`
* `spring-boot-starter-validation`
* `spring-boot-starter-security`
* `lombok` (for boilerplate reduction)
* `elasticsearch-java` client
* `slf4j` and `logback` (for logging)
* `junit 5` and `mockito` (for unit testing)
* `testcontainers` (for Elasticsearch integration testing)

## Testing the Whole Function

### Unit Tests
* **Models:**
    * Test `SearchableDocument`, `SearchRequest`, and `SearchResponse` behavior.
    * Verify calculated fields in `SearchResponse` (pagination metrics).
* **Service:**
    * Mock Elasticsearch operations using Mockito.
    * Test search functionality with different query parameters.
    * Verify health check behaviors for different Elasticsearch states.
* **Controller:**
    * Use MockMvc to test REST endpoints.
    * Validate security constraints and authentication.
    * Verify correct HTTP status codes and response structures.
* **Exception Handler:**
    * Test exception handling for different error scenarios.
    * Verify validation error mapping to response fields.

### Integration Tests
* Use Testcontainers to spin up Elasticsearch.
* Test actual document indexing and retrieval.
* Verify search functionality with real Elasticsearch queries.

### Security Testing
* Verify proper authentication requirements for endpoints.
* Test access control for different user roles.
* Ensure health endpoint has appropriate access controls.