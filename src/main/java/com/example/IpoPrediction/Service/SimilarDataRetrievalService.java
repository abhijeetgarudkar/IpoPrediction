package com.example.IpoPrediction.Service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimilarDataRetrievalService {
    private final EmbeddingService embeddingService;
    private final ChromaService chromaService;

    public SimilarDataRetrievalService(
            EmbeddingService embeddingService,
            ChromaService chromaService) {

        this.embeddingService = embeddingService;
        this.chromaService = chromaService;
    }

    public List<String> retrieve(String question) {

        // 1. Convert question into embedding
        List<Double> queryEmbedding =
                embeddingService.createEmbedding(question);

        // 2. Search Chroma
        return chromaService.search(
                queryEmbedding,
                20
        );
    }
}
