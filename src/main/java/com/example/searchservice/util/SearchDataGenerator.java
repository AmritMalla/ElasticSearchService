/**
 * Utility class to generate dummy search document data.
 * This can be used for testing, development, or initial data loading.
 */
package com.example.searchservice.util;

import com.example.searchservice.model.SearchableDocument;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SearchDataGenerator {

    private static final Random random = new Random();
    
    // Categories for documents
    private static final String[] CATEGORIES = {
        "Technology", "Science", "Business", "Finance", "Health", 
        "Sports", "Entertainment", "Politics", "Education", "Environment"
    };
    
    // Authors for documents
    private static final String[] AUTHORS = {
        "John Smith", "Emily Johnson", "Michael Brown", "Sarah Lee", 
        "David Wilson", "Jennifer Garcia", "Robert Martinez", "Linda Anderson", 
        "William Taylor", "Elizabeth Thomas"
    };
    
    // Tags for documents
    private static final String[] TAGS = {
        "research", "report", "analysis", "guide", "tutorial", 
        "review", "summary", "whitepaper", "case-study", "reference",
        "latest", "trending", "featured", "popular", "recommended"
    };
    
    // Sample titles
    private static final String[] TITLE_TEMPLATES = {
        "Comprehensive Guide to %s",
        "How to Master %s in 2025",
        "The Ultimate %s Handbook",
        "Understanding %s: A Deep Dive",
        "%s Fundamentals Explained",
        "Essential %s Strategies",
        "The Future of %s",
        "%s Best Practices",
        "Advanced %s Techniques",
        "%s: Trends and Insights"
    };
    
    // Sample content segments
    private static final String[] CONTENT_SEGMENTS = {
        "This comprehensive document explores the fundamental aspects of the subject matter. ",
        "In recent years, significant developments have transformed this field. ",
        "Experts agree that the most critical factor to consider is thorough research. ",
        "According to the latest studies, the trend is likely to continue into the next decade. ",
        "Several case studies demonstrate the effectiveness of this approach. ",
        "The data indicates a strong correlation between these variables. ",
        "Best practices suggest implementing a structured methodology. ",
        "Analysis of key metrics reveals important insights about performance. ",
        "A comparative assessment of different techniques shows varying results. ",
        "Future developments will likely focus on improving efficiency and scalability. ",
        "Integration with existing systems remains a significant challenge. ",
        "Stakeholders should consider multiple factors before making decisions. ",
        "The implementation strategy should account for potential risks. ",
        "Continuous monitoring and evaluation are essential for success. ",
        "Feedback from users has been incorporated into the recommendations. "
    };

    /**
     * Generate a specified number of random searchable documents.
     *
     * @param count Number of documents to generate
     * @return List of generated documents
     */
    public static List<SearchableDocument> generateRandomDocuments(int count) {
        List<SearchableDocument> documents = new ArrayList<>(count);
        
        for (int i = 0; i < count; i++) {
            documents.add(createRandomDocument());
        }
        
        return documents;
    }
    
    /**
     * Create a single random document with realistic data.
     *
     * @return Generated document
     */
    public static SearchableDocument createRandomDocument() {
        SearchableDocument document = new SearchableDocument();

        // Set unique ID
        document.setId(UUID.randomUUID().toString());

        // Set category
        String category = CATEGORIES[random.nextInt(CATEGORIES.length)];
        document.setCategory(category);

        // Set title based on category
        String titleTemplate = TITLE_TEMPLATES[random.nextInt(TITLE_TEMPLATES.length)];
        document.setTitle(String.format(titleTemplate, category));

        // Set content
        document.setContent(generateRandomContent(3 + random.nextInt(5)));

        // Set author
        document.setAuthor(AUTHORS[random.nextInt(AUTHORS.length)]);

        // Set tags (2-4 random tags)
        document.setTags(generateRandomTags(2 + random.nextInt(3)));

        // Set dates
        Date createdDate = generateRandomDate(365 * 2); // Within last 2 years
        document.setCreatedDate(createdDate);

        // Last updated between creation date and now
        long createdTime = createdDate.getTime();
        long now = System.currentTimeMillis();
        long updateTime = createdTime + random.nextInt();
        document.setLastUpdatedDate(new Date(updateTime));

        return document;
    }
    
    /**
     * Generate content by combining random segments.
     *
     * @param paragraphs Number of paragraphs to generate
     * @return Generated content text
     */
    private static String generateRandomContent(int paragraphs) {
        StringBuilder content = new StringBuilder();
        
        for (int i = 0; i < paragraphs; i++) {
            // Each paragraph has 3-6 sentences
            int sentences = 3 + random.nextInt(4);
            
            for (int j = 0; j < sentences; j++) {
                content.append(CONTENT_SEGMENTS[random.nextInt(CONTENT_SEGMENTS.length)]);
            }
            
            content.append("\n\n");
        }
        
        return content.toString().trim();
    }
    
    /**
     * Generate an array of random tags.
     *
     * @param count Number of tags to generate
     * @return Array of tag strings
     */
    private static String[] generateRandomTags(int count) {
        String[] result = new String[count];
        List<Integer> usedIndices = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            // Ensure we don't use the same tag twice
            int index;
            do {
                index = random.nextInt(TAGS.length);
            } while (usedIndices.contains(index));
            
            usedIndices.add(index);
            result[i] = TAGS[index];
        }
        
        return result;
    }
    
    /**
     * Generate a random date within the specified number of days before now.
     *
     * @param maxDaysAgo Maximum number of days in the past
     * @return Random date
     */
    private static Date generateRandomDate(int maxDaysAgo) {
        long now = System.currentTimeMillis();
        long randomTime = now - TimeUnit.DAYS.toMillis(random.nextInt(maxDaysAgo));
        return new Date(randomTime);
    }
}