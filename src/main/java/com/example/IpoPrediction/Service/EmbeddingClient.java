package com.example.IpoPrediction.Service;

import java.util.List;

public interface EmbeddingClient {

    List<Double> createEmbedding(String text);
}
