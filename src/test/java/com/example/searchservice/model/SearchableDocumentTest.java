package com.example.searchservice.model;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SearchableDocumentTest {

    @Test
    void testSearchableDocumentCreation() {
        SearchableDocument document = new SearchableDocument();
        assertNotNull(document, "Document should be created");
    }

    @Test
    void testSearchableDocumentGettersAndSetters() {
        // Create a document
        SearchableDocument document = new SearchableDocument();

        // Test ID
        String id = "doc123";
        document.setId(id);
        assertEquals(id, document.getId(), "ID should match");

        // Test title
        String title = "Test Document";
        document.setTitle(title);
        assertEquals(title, document.getTitle(), "Title should match");

        // Test content
        String content = "This is a test document content";
        document.setContent(content);
        assertEquals(content, document.getContent(), "Content should match");

        // Test author
        String author = "John Doe";
        document.setAuthor(author);
        assertEquals(author, document.getAuthor(), "Author should match");

        // Test dates
        Date createdDate = new Date();
        document.setCreatedDate(createdDate);
        assertEquals(createdDate, document.getCreatedDate(), "Created date should match");

        Date lastUpdatedDate = new Date();
        document.setLastUpdatedDate(lastUpdatedDate);
        assertEquals(lastUpdatedDate, document.getLastUpdatedDate(), "Last updated date should match");

        // Test category
        String category = "Test";
        document.setCategory(category);
        assertEquals(category, document.getCategory(), "Category should match");

        // Test tags
        String[] tags = {"test", "unit-test", "elasticsearch"};
        document.setTags(tags);
        assertArrayEquals(tags, document.getTags(), "Tags should match");

        // Test metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", 123);
        document.setMetadata(metadata);
        assertEquals(metadata, document.getMetadata(), "Metadata should match");
    }
}