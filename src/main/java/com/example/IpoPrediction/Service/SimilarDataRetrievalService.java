package com.example.IpoPrediction.Service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimilarDataRetrievalService {
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    public SimilarDataRetrievalService(EmbeddingService embeddingService, VectorStoreService vectorStoreService) {
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
    }

    public List<String> retrieve(String question) {

        // 1. Convert question into embedding
        List<Double> queryEmbedding =
                embeddingService.createEmbedding(question);

        // 2. Search Chroma
        return vectorStoreService.search(
                queryEmbedding,
                20
        );
    }
}
