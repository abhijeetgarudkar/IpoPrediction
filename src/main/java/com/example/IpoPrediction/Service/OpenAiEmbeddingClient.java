package com.example.IpoPrediction.Service;

import com.example.IpoPrediction.DO.EmbeddingResponse;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

public class OpenAiEmbeddingClient implements EmbeddingClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String embeddingModel;

    public OpenAiEmbeddingClient(
            RestClient.Builder builder,
            String baseUrl,
            String apiKey,
            String embeddingModel) {
        this.restClient = builder.clone().baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public List<Double> createEmbedding(String text) {
        Map<String, Object> request = Map.of(
                "model", embeddingModel,
                "input", text
        );

        EmbeddingResponse response = restClient.post()
                .uri("/v1/embeddings")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(request)
                .retrieve()
                .body(EmbeddingResponse.class);

        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            throw new IllegalStateException("OpenAI returned no embeddings");
        }

        return response.getData().get(0).getEmbedding();
    }
}
