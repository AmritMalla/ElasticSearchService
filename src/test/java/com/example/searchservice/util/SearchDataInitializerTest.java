package com.example.searchservice.util;

import com.example.searchservice.repository.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SearchDataInitializer class.
 *
 * These tests verify the behavior of the sample data loading functionality, ensuring
 * it correctly initializes data when needed, skips initialization when data exists,
 * and handles exceptions gracefully.
 */
class SearchDataInitializerTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchDataInitializer searchDataInitializer;

    /**
     * Sets up the test environment before each test case.
     *
     * Initializes Mockito annotations and configures the SearchDataInitializer with test values
     * for data initialization flag and document count.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(searchDataInitializer, "shouldInitializeData", true);
        ReflectionTestUtils.setField(searchDataInitializer, "documentCount", 10);
    }

    /**
     * Tests the loading of sample data when no documents exist in the repository.
     *
     * Expected behavior: should call the repository to save a list of documents when the count is zero.
     */
    @Test
    void testLoadSampleDataWhenNoDocumentsExist() {
        // Arrange
        when(searchRepository.count()).thenReturn(0L);

        // Act
        searchDataInitializer.loadSampleData();

        // Assert
        verify(searchRepository).count();
        verify(searchRepository).saveAll(anyList());
    }

    /**
     * Tests the loading of sample data when documents already exist in the repository.
     *
     * Expected behavior: should not attempt to save any documents if the repository already contains data.
     */
    @Test
    void testLoadSampleDataWhenDocumentsAlreadyExist() {
        // Arrange
        when(searchRepository.count()).thenReturn(100L);

        // Act
        searchDataInitializer.loadSampleData();

        // Assert
        verify(searchRepository).count();
        verify(searchRepository, never()).saveAll(anyList());
    }

    /**
     * Tests the loading of sample data when an exception occurs during the process.
     *
     * Expected behavior: should handle the exception gracefully without throwing it and avoid saving data.
     */
    @Test
    void testLoadSampleDataHandlesExceptions() {
        // Arrange
        when(searchRepository.count()).thenThrow(new RuntimeException("Test exception"));

        // Act - should not throw exception
        assertDoesNotThrow(() -> searchDataInitializer.loadSampleData());

        // Assert
        verify(searchRepository).count();
        verify(searchRepository, never()).saveAll(anyList());
    }
}