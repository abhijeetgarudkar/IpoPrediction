package com.example.IpoPrediction.Service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private final EmbeddingClient embeddingClient;

    public EmbeddingService(EmbeddingClient embeddingClient) {
        this.embeddingClient = embeddingClient;
    }

    public List<Double> createEmbedding(String text) {
        return embeddingClient.createEmbedding(text);
    }
}
