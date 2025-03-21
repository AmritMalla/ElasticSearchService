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

/**
 * Generates random SearchableDocument instances for testing and development purposes.
 */
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

    // Sample titles with placeholder for category
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

    // Sample content segments for building document content
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
     * Generates a specified number of random searchable documents.
     *
     * @param count the number of documents to generate
     * @return a list of randomly generated SearchableDocument instances
     */
    public static List<SearchableDocument> generateRandomDocuments(int count) {
        List<SearchableDocument> documents = new ArrayList<>(count);  // Pre-allocate list with expected size

        // Generate the requested number of documents
        for (int i = 0; i < count; i++) {
            documents.add(createRandomDocument());
        }

        return documents;
    }

    /**
     * Creates a single random document with realistic attributes.
     *
     * @return a newly generated SearchableDocument instance
     */
    public static SearchableDocument createRandomDocument() {
        SearchableDocument document = new SearchableDocument();

        // Assign a unique identifier
        document.setId(UUID.randomUUID().toString());

        // Select a random category
        String category = CATEGORIES[random.nextInt(CATEGORIES.length)];
        document.setCategory(category);

        // Generate a title using a template and the selected category
        String titleTemplate = TITLE_TEMPLATES[random.nextInt(TITLE_TEMPLATES.length)];
        document.setTitle(String.format(titleTemplate, category));

        // Generate random content with varying paragraph count
        document.setContent(generateRandomContent(3 + random.nextInt(5)));

        // Assign a random author
        document.setAuthor(AUTHORS[random.nextInt(AUTHORS.length)]);

        // Assign 2-4 random unique tags
        document.setTags(generateRandomTags(2 + random.nextInt(3)));

        // Generate a creation date within the last 2 years
        Date createdDate = generateRandomDate(365 * 2);
        document.setCreatedDate(createdDate);

        // Generate a last updated date between creation and now
        long createdTime = createdDate.getTime();
        long now = System.currentTimeMillis();
        long updateTime = createdTime + Math.abs(random.nextLong() % (now - createdTime));  // Ensure update is after creation
        document.setLastUpdatedDate(new Date(updateTime));

        return document;
    }

    /**
     * Generates random content by combining content segments into paragraphs.
     *
     * @param paragraphs the number of paragraphs to generate
     * @return a string containing the generated content
     */
    private static String generateRandomContent(int paragraphs) {
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < paragraphs; i++) {
            // Generate 3-6 sentences per paragraph
            int sentences = 3 + random.nextInt(4);

            for (int j = 0; j < sentences; j++) {
                content.append(CONTENT_SEGMENTS[random.nextInt(CONTENT_SEGMENTS.length)]);
            }

            content.append("\n\n");  // Separate paragraphs with double newline
        }

        return content.toString().trim();  // Remove trailing whitespace
    }

    /**
     * Generates an array of unique random tags.
     *
     * @param count the number of tags to generate
     * @return an array of randomly selected unique tags
     */
    private static String[] generateRandomTags(int count) {
        String[] result = new String[count];
        List<Integer> usedIndices = new ArrayList<>();  // Track used indices to ensure uniqueness

        for (int i = 0; i < count; i++) {
            int index;
            do {
                index = random.nextInt(TAGS.length);
            } while (usedIndices.contains(index));  // Repeat until we find an unused tag

            usedIndices.add(index);
            result[i] = TAGS[index];
        }

        return result;
    }

    /**
     * Generates a random date within a specified number of days before the current time.
     *
     * @param maxDaysAgo the maximum number of days in the past for the generated date
     * @return a randomly generated Date object
     */
    private static Date generateRandomDate(int maxDaysAgo) {
        long now = System.currentTimeMillis();
        long randomTime = now - TimeUnit.DAYS.toMillis(random.nextInt(maxDaysAgo));
        return new Date(randomTime);
    }
}