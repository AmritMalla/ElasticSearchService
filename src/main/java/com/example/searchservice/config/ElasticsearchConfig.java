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

@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    /**
     * Host URL for Elasticsearch cluster from application properties.
     */
    @Value("${elasticsearch.host}")
    private String host;

    /**
     * Connection timeout in milliseconds from application properties.
     */
    @Value("${elasticsearch.connection.timeout:5000}")
    private int connectionTimeout;

    /**
     * Socket timeout in milliseconds from application properties.
     */
    @Value("${elasticsearch.socket.timeout:10000}")
    private int socketTimeout;

    /**
     * Creates and configures the Elasticsearch RestHighLevelClient.
     * This client is used to communicate with the Elasticsearch cluster.
     *
     * @return Configured RestHighLevelClient instance
     */
    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        // Configure connection parameters including timeouts
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(host)
                .withConnectTimeout(Duration.ofMillis(connectionTimeout))
                .withSocketTimeout(Duration.ofMillis(socketTimeout))
                .build();
        
        // Create and return the client using the configuration
        return RestClients.create(clientConfiguration).rest();
    }

    /**
     * Creates the ElasticsearchOperations bean used for template operations.
     * This provides a higher-level API to interact with Elasticsearch.
     *
     * @return ElasticsearchOperations instance
     */
    @Bean
    public ElasticsearchOperations elasticsearchOperations() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}