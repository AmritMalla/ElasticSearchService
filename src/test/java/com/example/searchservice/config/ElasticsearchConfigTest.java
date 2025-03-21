package com.example.searchservice.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for {@link ElasticsearchConfig}.
 *
 * <p>Verifies proper bean creation and injection of Elasticsearch-related components
 * such as {@link RestHighLevelClient} and {@link ElasticsearchOperations}.</p>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "elasticsearch.host=localhost:9200",
        "elasticsearch.connection.timeout=3000",
        "elasticsearch.socket.timeout=5000"
})
class ElasticsearchConfigTest {

    @Autowired
    private ElasticsearchConfig elasticsearchConfig;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    /**
     * Test: Ensure that the {@link ElasticsearchConfig} bean is loaded into the context.
     */
    @Test
    void elasticsearchConfigShouldNotBeNull() {
        assertNotNull(elasticsearchConfig, "ElasticsearchConfig should be initialized");
    }

    /**
     * Test: Validate that the RestHighLevelClient is successfully created from config.
     * Ensures that {@link ElasticsearchConfig#elasticsearchClient()} does not return null.
     */
    @Test
    void elasticsearchClientShouldBeCreated() {
        // Create client using config
        RestHighLevelClient client = elasticsearchConfig.elasticsearchClient();

        // Verify it's not null
        assertNotNull(client, "Elasticsearch client should be created");
    }

    /**
     * Test: Verify that the {@link RestHighLevelClient} is auto-injected by Spring.
     */
    @Test
    void restHighLevelClientShouldBeInjected() {
        assertNotNull(restHighLevelClient, "RestHighLevelClient should be injected");
    }

    /**
     * Test: Verify that the {@link ElasticsearchOperations} bean is injected by Spring.
     */
    @Test
    void elasticsearchOperationsShouldBeInjected() {
        assertNotNull(elasticsearchOperations, "ElasticsearchOperations should be injected");
    }

    /**
     * Test: Validate that the {@link ElasticsearchOperations} bean is created through the config class.
     * Ensures {@link ElasticsearchConfig#elasticsearchOperations()} returns a valid instance.
     */
    @Test
    void elasticsearchOperationsFromConfigShouldNotBeNull() {
        // Create operations instance using config
        ElasticsearchOperations operations = elasticsearchConfig.elasticsearchOperations();

        // Validate it's not null
        assertNotNull(operations, "ElasticsearchOperations from config should not be null");
    }
}
