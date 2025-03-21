package com.example.searchservice.util;

import com.example.searchservice.model.SearchableDocument;
import com.example.searchservice.repository.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchDataInitializerTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchDataInitializer searchDataInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(searchDataInitializer, "shouldInitializeData", true);
        ReflectionTestUtils.setField(searchDataInitializer, "documentCount", 10);
    }

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