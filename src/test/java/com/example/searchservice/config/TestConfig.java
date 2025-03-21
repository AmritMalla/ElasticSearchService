package com.example.searchservice.config;

import com.example.searchservice.repository.SearchRepository;
import org.elasticsearch.client.RestHighLevelClient;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

/**
 * Test configuration class for Elasticsearch-related beans.
 * Overrides production beans with mocks and provides simplified setup for testing.
 */
@TestConfiguration
@EnableAutoConfiguration(exclude = {
        ElasticsearchDataAutoConfiguration.class,
        ElasticsearchRepositoriesAutoConfiguration.class
})
public class TestConfig {

    /**
     * Mocked repository bean to avoid hitting the actual Elasticsearch backend.
     * Allows injection of SearchRepository in test contexts without real data interaction.
     */
    @MockBean
    private SearchRepository searchRepository;

    /**
     * Provides a mocked RestHighLevelClient for tests.
     *
     * @return mocked RestHighLevelClient
     */
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        return Mockito.mock(RestHighLevelClient.class);
    }

    /**
     * Provides a mocked ElasticsearchOperations implementation for use in tests.
     * The name "elasticsearchTemplate" is used to match any component expecting this specific bean name.
     *
     * @return mocked ElasticsearchRestTemplate
     */
    @Bean(name = "elasticsearchTemplate")
    public ElasticsearchOperations elasticsearchTemplate() {
        return Mockito.mock(ElasticsearchRestTemplate.class);
    }

    /**
     * Provides an ElasticsearchConverter bean used for mapping entities in tests.
     *
     * @return MappingElasticsearchConverter instance
     */
    @Bean
    public ElasticsearchConverter elasticsearchConverter() {
        return new MappingElasticsearchConverter(elasticsearchMappingContext());
    }

    /**
     * Provides a simplified mapping context for entity-to-index mapping.
     *
     * @return SimpleElasticsearchMappingContext instance
     */
    @Bean
    public SimpleElasticsearchMappingContext elasticsearchMappingContext() {
        return new SimpleElasticsearchMappingContext();
    }
}
