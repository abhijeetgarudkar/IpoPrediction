package com.example.IpoPrediction.Service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
public class IpoIngestionService {
    private final IPODetailsWebScraperService scraper;
    private final TextChunkerService chunker;
    private final EmbeddingService embedding;
    private final ChromaService chroma;
    private final IngestGuard ingestGuard;

    public IpoIngestionService(
            IPODetailsWebScraperService scraper,
            TextChunkerService chunker,
            EmbeddingService embedding,
            ChromaService chroma,
            IngestGuard ingestGuard) {
        this.scraper = scraper;
        this.chunker = chunker;
        this.embedding = embedding;
        this.chroma = chroma;
        this.ingestGuard = ingestGuard;
    }

    public void ingest(String url) {
        URI uri = ingestGuard.validateUrl(url);
        String source = uri.toString();

        chroma.deleteBySource(source);

        String content;
        try {
            content = scraper.scrape(source);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to scrape IPO URL", e);
        }

        List<String> chunks = chunker.chunk(content, 1000, 200);
        String sourceKey = sourceKey(source);

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            List<Double> vector = embedding.createEmbedding(chunk);
            chroma.store(
                    "ipo-" + sourceKey + "-" + i,
                    chunk,
                    vector,
                    source
            );
        }
    }

    static String sourceKey(String source) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(source.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
