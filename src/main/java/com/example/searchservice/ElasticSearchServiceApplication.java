/**
 * Main Application class for Elasticsearch Search Microservice.
 * Serves as the entry point for the Spring Boot application.
 */
package com.example.searchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.example.searchservice.repository")
public class ElasticSearchServiceApplication {

	/**
	 * Main method that starts the Spring Boot application.
	 *
	 * @param args Command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(ElasticSearchServiceApplication.class, args);
	}
}