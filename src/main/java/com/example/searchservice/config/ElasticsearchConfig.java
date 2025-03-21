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
