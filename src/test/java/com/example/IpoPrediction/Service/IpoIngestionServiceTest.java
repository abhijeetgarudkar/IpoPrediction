package com.example.IpoPrediction.Service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class IpoIngestionServiceTest {

    @Test
    void sourceKeysDifferByUrl() {
        assertNotEquals(
                IpoIngestionService.sourceKey("https://example.com/a"),
                IpoIngestionService.sourceKey("https://example.com/b")
        );
        assertEquals(
                IpoIngestionService.sourceKey("https://example.com/a"),
                IpoIngestionService.sourceKey("https://example.com/a")
        );
    }
}
