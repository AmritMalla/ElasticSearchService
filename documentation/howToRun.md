## How to Run

### 1. Create the Project Structure
Clone the project or set up the directory structure as shown in the folder tree. The project follows a standard Spring Boot application layout with packages for controllers, services, repositories, models, exceptions, and configuration classes.

### 2. Dependencies (Gradle Configuration)
Ensure your `build.gradle` file includes all required dependencies. The project uses Spring Boot 2.7 with Java 11 and includes the following key dependencies:

```gradle
dependencies {
    // Spring Boot core dependencies
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-security'

    // Elasticsearch dependencies
    implementation 'org.springframework.boot:spring-boot-starter-data-elasticsearch'
    implementation 'co.elastic.clients:elasticsearch-java:8.11.1'
    implementation ('org.json:json:20231013') {
        exclude group: 'com.vaadin.external.google', module: 'android-json'
    }

    // Lombok for boilerplate reduction
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // Testing dependencies
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'org.testcontainers:elasticsearch:1.19.3'
}
```

### 3. Configure Elasticsearch and Application Properties
Configure your Elasticsearch connection in the `application.properties` file:

```properties
# Elasticsearch configuration
elasticsearch.host=localhost:9200
elasticsearch.index.name=documents
elasticsearch.connection.timeout=5000
elasticsearch.socket.timeout=10000

# For generation and initialization of Elasticsearch Collection
spring.profiles.active=dev
elasticsearch.data.initialize=true
elasticsearch.data.count=500

# Server configuration
server.port=8080
server.servlet.context-path=/

# Spring security configuration
spring.security.user.name=elastic
spring.security.user.password=elastic
```

Make sure Elasticsearch is installed and running on the configured host and port.

### 4. Prepare Elasticsearch Settings
The application uses custom Elasticsearch analyzers defined in `elasticsearch-settings.json`:

```json
{
  "index": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "refresh_interval": "1s"
  },
  "analysis": {
    "analyzer": {
      "custom_analyzer": {
        "type": "custom",
        "tokenizer": "standard",
        "filter": [
          "lowercase",
          "asciifolding",
          "stop",
          "snowball"
        ]
      }
    },
    "filter": {
      "snowball": {
        "type": "snowball",
        "language": "English"
      }
    }
  }
}
```

### 5. Build & Test the Application
From the root directory, run:

```bash
./gradlew clean build
```

To run unit tests:

```bash
./gradlew test
```

The project has a high test coverage requirement of 90%, which is configured in the build.gradle file.

### 6. Start the Application
Run the Spring Boot service:

```bash
./gradlew bootRun
```

Or, using Java:

```bash
java -jar build/libs/elasticsearchservice-1.0.0-SNAPSHOT.jar
```

By default, the service runs on port 8080.

### 7. Access the API Endpoints
Use cURL or Postman to interact with the search API:

* Health check (no authentication required):
```bash
curl -X GET "http://localhost:8080/api/health"
```

* Perform a search (requires authentication):
```bash
curl -X POST "http://localhost:8080/api/search" \
  -H "Content-Type: application/json" \
  -u elastic:elastic \
  -d '{"query": "technology", "page": 0, "size": 10}'
```

Example search request body:
```json
{
  "query": "technology",
  "fields": ["title", "content"],
  "page": 0,
  "size": 10,
  "sort": {
    "createdDate": "desc"
  },
  "filters": {
    "category": "Technology" 
  },
  "dateFrom": "2023-01-01",
  "dateTo": "2023-12-31",
  "dateField": "createdDate",
  "minScore": 0.5
}
```

## Architecture Overview

### 1. Main Components

#### Models
- **SearchableDocument**: Document structure stored in Elasticsearch
- **SearchRequest**: Parameters for search operations
- **SearchResponse**: Structure for returning search results
- **ErrorResponse**: Standardized error response format

#### Controllers
- **SearchController**: REST endpoint for search operations

#### Services
- **SearchService**: Interface defining search operations
- **SearchServiceImpl**: Implementation of search logic

#### Repository
- **SearchRepository**: Interface extending ElasticsearchRepository

#### Configuration
- **ElasticsearchConfig**: Connection settings for Elasticsearch
- **SecurityConfig**: HTTP security configuration

#### Exception Handling
- **ElasticsearchQueryException**: Custom exception for query errors
- **GlobalExceptionHandler**: Centralized error handling

### 2. Security
The application uses Spring Security with Basic Authentication:
- Public endpoints: `/api/health`
- Protected endpoints: `/api/search/**` (requires authentication)
- Default credentials: username `elastic`, password `elastic`

### 3. Sample Data Generation
For development and testing, the application can initialize Elasticsearch with sample documents:
- Configured in `application.properties` with `elasticsearch.data.initialize=true`
- Generated data includes randomized titles, content, authors, categories, and tags
- Only runs in development and test profiles

### 4. Performance Considerations
- Connection pool configuration for Elasticsearch
- Pagination for search results
- Performance logging for search operations
- Custom analyzers for better search accuracy

## Time and Space Complexity Analysis

- **Search operation (O(log n))**: Elasticsearch uses inverted indices for efficient search
- **Document indexing (O(log n))**: Adding documents to Elasticsearch has logarithmic complexity
- **Pagination (O(1))**: Retrieving a page of results has constant time complexity
- **Filter and sort operations**: These operations are optimized by Elasticsearch's internal data structures

## Conclusion

The ElasticSearch Service provides a robust and scalable solution for text search capabilities using Spring Boot and Elasticsearch. Key features include:

- Full-text search with customized analyzers
- Filtering, sorting, and pagination
- Security with basic authentication
- Comprehensive error handling
- Test coverage at 90%+
- Sample data generation for development
