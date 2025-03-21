package com.example.searchservice.util;

import com.example.searchservice.model.SearchableDocument;
import com.example.searchservice.repository.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchDataInitializerTest {

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private ApplicationReadyEvent applicationReadyEvent;

    @InjectMocks
    private SearchDataInitializer searchDataInitializer;

    @BeforeEach
    void setUp() {
        // Set default property values
        ReflectionTestUtils.setField(searchDataInitializer, "shouldInitializeData", true);
        ReflectionTestUtils.setField(searchDataInitializer, "documentCount", 50);
    }

    @Test
    void testLoadSampleDataWhenInitializationEnabled() {
        // Setup mocks
        when(searchRepository.count()).thenReturn(0L);

        List<SearchableDocument> documents = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            documents.add(new SearchableDocument());
        }

        try (var mockedStatic = mockStatic(SearchDataGenerator.class)) {
            mockedStatic.when(() -> SearchDataGenerator.generateRandomDocuments(anyInt()))
                    .thenReturn(documents);

            // Execute
            searchDataInitializer.loadSampleData();

            // Verify
            verify(searchRepository).count();
            verify(searchRepository).saveAll(documents);
            mockedStatic.verify(() -> SearchDataGenerator.generateRandomDocuments(50));
        }
    }

    @Test
    void testLoadSampleDataWhenInitializationDisabled() {
        // Disable initialization
        ReflectionTestUtils.setField(searchDataInitializer, "shouldInitializeData", false);

        // Execute
        searchDataInitializer.loadSampleData();

        // Verify
        verify(searchRepository, never()).count();
        verify(searchRepository, never()).saveAll(anyList());
    }

    @Test
    void testLoadSampleDataWhenDocumentsAlreadyExist() {
        // Setup mocks - index already has data
        when(searchRepository.count()).thenReturn(100L);

        // Execute
        searchDataInitializer.loadSampleData();

        // Verify
        verify(searchRepository).count();
        verify(searchRepository, never()).saveAll(anyList());
    }

    @Test
    void testLoadSampleDataWithException() {
        // Setup mocks to throw exception
        when(searchRepository.count()).thenThrow(new RuntimeException("Test exception"));

        // Execute - should not throw
        searchDataInitializer.loadSampleData();

        // Verify
        verify(searchRepository).count();
        verify(searchRepository, never()).saveAll(anyList());
    }
}