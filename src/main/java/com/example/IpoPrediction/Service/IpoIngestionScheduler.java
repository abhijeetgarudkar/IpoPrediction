package com.example.IpoPrediction.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(
        name = "ingest.schedule.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class IpoIngestionScheduler {

    private static final Logger log = LoggerFactory.getLogger(IpoIngestionScheduler.class);

    private final IpoIngestionService ingestionService;
    private final String scheduledUrls;

    public IpoIngestionScheduler(
            IpoIngestionService ingestionService,
            @Value("${ingest.scheduled.urls:}") String scheduledUrls) {
        this.ingestionService = ingestionService;
        this.scheduledUrls = scheduledUrls;
    }

    @Scheduled(
            fixedDelayString = "${ingest.schedule.fixed-delay-ms:600000}",
            initialDelayString = "${ingest.schedule.initial-delay-ms:10000}"
    )
    public void runScheduledIngestion() {
        List<String> urls = parseUrls(scheduledUrls);

        if (urls.isEmpty()) {
            log.info("Scheduled IPO ingestion skipped: no URLs configured in ingest.scheduled.urls");
            return;
        }

        log.info("Starting scheduled IPO ingestion for {} URL(s)", urls.size());

        for (String url : urls) {
            try {
                log.info("Ingesting IPO data from URL: {}", url);
                ingestionService.ingest(url);
                log.info("Successfully ingested IPO data from URL: {}", url);
            } catch (Exception e) {
                log.error("Failed scheduled ingestion for URL: {}", url, e);
            }
        }

        log.info("Completed scheduled IPO ingestion cycle");
    }

    static List<String> parseUrls(String rawUrls) {
        if (rawUrls == null || rawUrls.isBlank()) {
            return List.of();
        }

        return Arrays.stream(rawUrls.split(","))
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .toList();
    }
}
