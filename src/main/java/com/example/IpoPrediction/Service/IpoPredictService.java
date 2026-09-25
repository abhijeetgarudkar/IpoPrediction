package com.example.IpoPrediction.Service;

import com.example.IpoPrediction.DO.LLMResponse;
import com.example.IpoPrediction.DO.OpenAiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class IpoPredictService {

    private final SimilarDataRetrievalService retrievalService;
    private final RestClient restClient;
    private final String apiKey;
    private final String systemPrompt;
    private final String model;

    public IpoPredictService(
            SimilarDataRetrievalService retrievalService,
            RestClient.Builder builder,
            @Value("${openai.base-url}") String openAiBaseUrl,
            @Value("${openai.api.key}") String apiKey,
            @Value("${llm.system-prompt}") String systemPrompt,
            @Value("${openai.model}") String model) {
        this.retrievalService = retrievalService;
        this.restClient = builder.clone().baseUrl(openAiBaseUrl).build();
        this.apiKey = apiKey;
        this.systemPrompt = systemPrompt;
        this.model = model;
    }

    public LLMResponse predict(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query is required");
        }

        List<String> relevantChunks = retrievalService.retrieve(query);

        String context = String.join("\n\n", relevantChunks);

        String prompt = """
            %s

            Context from the IPO data source:
            %s

            User question:
            %s
            """.formatted(
                systemPrompt,
                context,
                query
        );

        Map<String, Object> request = Map.of(
                "model", model,
                "instructions", systemPrompt,
                "input", prompt
        );

        OpenAiResponse response = restClient.post()
                .uri("/v1/responses")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(request)
                .retrieve()
                .body(OpenAiResponse.class);

        if (response == null || response.output() == null) {
            return new LLMResponse("No response generated");
        }

        return response.output()
                .stream()
                .filter(output -> "message".equals(output.type()))
                .flatMap(output -> output.content() == null ? Stream.empty() : output.content().stream())
                .filter(content -> "output_text".equals(content.type()))
                .map(OpenAiResponse.Content::text)
                .findFirst()
                .map(LLMResponse::new)
                .orElse(new LLMResponse("No response generated"));
    }
}
