package com.example.IpoPrediction.Service;

import java.util.List;

public interface VectorStoreService {

    void store(
            String id,
            String document,
            List<Double> embedding,
            String source
    );

    List<String> search(
            List<Double> queryEmbedding,
            int topK
    );

    void deleteBySource(String source);
}
