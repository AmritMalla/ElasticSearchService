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

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void elasticsearchConfigShouldNotBeNull() {
        assertNotNull(elasticsearchConfig, "ElasticsearchConfig should be initialized");
    }

    @Test
    void elasticsearchClientShouldBeCreated() {
        RestHighLevelClient client = elasticsearchConfig.elasticsearchClient();
        assertNotNull(client, "Elasticsearch client should be created");
    }

    @Test
    void restHighLevelClientShouldBeInjected() {
        assertNotNull(restHighLevelClient, "RestHighLevelClient should be injected");
    }

    @Test
    void elasticsearchOperationsShouldBeInjected() {
        assertNotNull(elasticsearchOperations, "ElasticsearchOperations should be injected");
    }

    @Test
    void elasticsearchOperationsFromConfigShouldNotBeNull() {
        ElasticsearchOperations operations = elasticsearchConfig.elasticsearchOperations();
        assertNotNull(operations, "ElasticsearchOperations from config should not be null");
    }
}