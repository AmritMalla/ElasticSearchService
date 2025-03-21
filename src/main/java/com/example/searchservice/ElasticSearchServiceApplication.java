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