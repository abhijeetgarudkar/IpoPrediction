package com.example.IpoPrediction.Service;

import com.example.IpoPrediction.DO.OllamaEmbeddingResponse;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

public class OllamaEmbeddingClient implements EmbeddingClient {

    private final RestClient restClient;
    private final String embeddingModel;

    public OllamaEmbeddingClient(RestClient.Builder builder, String baseUrl, String embeddingModel) {
        this.restClient = builder.clone().baseUrl(baseUrl).build();
        this.embeddingModel = embeddingModel;
    }

    @Override
    public List<Double> createEmbedding(String text) {
        Map<String, Object> request = Map.of(
                "model", embeddingModel,
                "input", text
        );

        OllamaEmbeddingResponse response = restClient.post()
                .uri("/api/embed")
                .header("Content-Type", "application/json")
                .body(request)
                .retrieve()
                .body(OllamaEmbeddingResponse.class);

        if (response == null || response.getEmbeddings() == null || response.getEmbeddings().isEmpty()) {
            throw new IllegalStateException("Ollama returned no embeddings");
        }
        return response.getEmbeddings().get(0);
    }
}
