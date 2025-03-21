package com.example.searchservice.util;

import com.example.searchservice.model.SearchableDocument;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SearchDataGenerator class.
 *
 * These tests verify the behavior of the random document generation utilities, ensuring
 * that generated documents meet the expected criteria for count, field presence, and validity.
 */
class SearchDataGeneratorTest {

    /**
     * Tests the generation of a list of random documents with varying counts.
     *
     * Expected behavior: should return an empty list for a count of zero and a correctly sized list
     * with valid documents for a positive count, where all required fields are non-null.
     */
    @Test
    void testGenerateRandomDocuments() {
        // Test with zero count
        List<SearchableDocument> emptyList = SearchDataGenerator.generateRandomDocuments(0);
        assertEquals(0, emptyList.size(), "Should return empty list for count 0");

        // Test with positive count
        int count = 5;
        List<SearchableDocument> documents = SearchDataGenerator.generateRandomDocuments(count);

        assertNotNull(documents, "Generated list should not be null");
        assertEquals(count, documents.size(), "Generated list size should match requested count");

        // Each document should have non-null required fields
        for (SearchableDocument doc : documents) {
            assertNotNull(doc.getId(), "Document ID should not be null");
            assertNotNull(doc.getTitle(), "Document title should not be null");
            assertNotNull(doc.getContent(), "Document content should not be null");
            assertNotNull(doc.getAuthor(), "Document author should not be null");
            assertNotNull(doc.getCategory(), "Document category should not be null");
            assertNotNull(doc.getTags(), "Document tags should not be null");
            assertNotNull(doc.getCreatedDate(), "Document created date should not be null");
            assertNotNull(doc.getLastUpdatedDate(), "Document last updated date should not be null");
        }
    }

    /**
     * Tests the creation of a single random document.
     *
     * Expected behavior: should return a valid SearchableDocument with non-null fields,
     * a title containing the category, a valid tag range, and dates in the past or present.
     */
    @Test
    void testCreateRandomDocument() {
        // Generate a single document
        SearchableDocument document = SearchDataGenerator.createRandomDocument();

        // Basic validation
        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getId(), "ID should not be null");
        assertTrue(document.getId().length() > 0, "ID should not be empty");

        // Title should contain the category
        assertNotNull(document.getTitle(), "Title should not be null");
        assertNotNull(document.getCategory(), "Category should not be null");
        assertTrue(document.getTitle().contains(document.getCategory()),
                "Title should contain the category");

        // Content validation
        assertNotNull(document.getContent(), "Content should not be null");
        assertTrue(document.getContent().length() > 0, "Content should not be empty");

        // Tags validation
        String[] tags = document.getTags();
        assertNotNull(tags, "Tags should not be null");
        assertTrue(tags.length >= 2, "Should have at least 2 tags");
        assertTrue(tags.length <= 4, "Should have at most 4 tags");

        // No tag should be null
        for (String tag : tags) {
            assertNotNull(tag, "Tag should not be null");
        }

        // Date validation
        Date createdDate = document.getCreatedDate();
        Date lastUpdatedDate = document.getLastUpdatedDate();
        Date now = new Date();

        assertNotNull(createdDate, "Created date should not be null");
        assertNotNull(lastUpdatedDate, "Last updated date should not be null");

        // Dates should be in the past
        assertTrue(createdDate.before(now) || createdDate.equals(now),
                "Created date should not be in the future");
        assertTrue(lastUpdatedDate.before(now) || lastUpdatedDate.equals(now),
                "Last updated date should not be in the future");
    }
}