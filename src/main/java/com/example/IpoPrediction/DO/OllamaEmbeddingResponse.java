package com.example.IpoPrediction.DO;

import java.util.List;

public class OllamaEmbeddingResponse {

    private List<List<Double>> embeddings;

    public List<List<Double>> getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(List<List<Double>> embeddings) {
        this.embeddings = embeddings;
    }
}
