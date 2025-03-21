package com.example.searchservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ElasticSearchServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should load");
    }

    @Test
    void mainMethodShouldRun() {
        // This test just verifies the main method doesn't throw an exception
        assertDoesNotThrow(() -> {
            ElasticSearchServiceApplication.main(new String[]{});
        }, "Main method should run without exceptions");
    }
}