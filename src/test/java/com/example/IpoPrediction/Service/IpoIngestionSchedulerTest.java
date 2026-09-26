package com.example.IpoPrediction.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpoIngestionSchedulerTest {

    @Mock
    private IpoIngestionService ingestionService;

    @Test
    void runsScheduledIngestionForConfiguredUrls() {
        IpoIngestionScheduler scheduler = new IpoIngestionScheduler(
                ingestionService,
                "https://example.com/ipo1, https://example.com/ipo2"
        );

        scheduler.runScheduledIngestion();

        verify(ingestionService, times(1)).ingest("https://example.com/ipo1");
        verify(ingestionService, times(1)).ingest("https://example.com/ipo2");
        verifyNoMoreInteractions(ingestionService);
    }

    @Test
    void skipsIngestionWhenUrlsAreEmptyOrBlank() {
        IpoIngestionScheduler scheduler = new IpoIngestionScheduler(ingestionService, "   ");

        scheduler.runScheduledIngestion();

        verifyNoInteractions(ingestionService);
    }

    @Test
    void continuesIngestionWhenOneUrlFails() {
        doThrow(new RuntimeException("Network error"))
                .when(ingestionService).ingest("https://example.com/fail");

        IpoIngestionScheduler scheduler = new IpoIngestionScheduler(
                ingestionService,
                "https://example.com/fail, https://example.com/ok"
        );

        scheduler.runScheduledIngestion();

        verify(ingestionService, times(1)).ingest("https://example.com/fail");
        verify(ingestionService, times(1)).ingest("https://example.com/ok");
    }

    @Test
    void parseUrlsTrimsAndFiltersBlankEntries() {
        List<String> urls = IpoIngestionScheduler.parseUrls("  https://a.com , , https://b.com/123 ");
        assertEquals(List.of("https://a.com", "https://b.com/123"), urls);

        assertEquals(List.of(), IpoIngestionScheduler.parseUrls(null));
        assertEquals(List.of(), IpoIngestionScheduler.parseUrls("   "));
    }
}
