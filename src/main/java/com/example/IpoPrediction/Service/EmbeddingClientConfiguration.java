package com.example.IpoPrediction.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class EmbeddingClientConfiguration {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingClientConfiguration.class);

    @Bean
    public EmbeddingClient embeddingClient(
            RestClient.Builder builder,
            @Value("${app.environment:dev}") String environment,
            @Value("${ollama.base-url}") String ollamaBaseUrl,
            @Value("${ollama.embedding.model}") String ollamaEmbeddingModel,
            @Value("${openai.base-url}") String openAiBaseUrl,
            @Value("${openai.api.key}") String openAiApiKey,
            @Value("${openai.embedding.model}") String openAiEmbeddingModel) {

        if (AppEnvironment.isLocalOrDev(environment)) {
            log.info("Using Ollama embeddings for environment '{}'", environment);
            return new OllamaEmbeddingClient(builder, ollamaBaseUrl, ollamaEmbeddingModel);
        }

        log.info("Using OpenAI embeddings for environment '{}'", environment);
        return new OpenAiEmbeddingClient(builder, openAiBaseUrl, openAiApiKey, openAiEmbeddingModel);
    }
}
