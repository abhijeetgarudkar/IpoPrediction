package com.example.IpoPrediction.Service;

import com.example.IpoPrediction.DO.ChromaSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(
        name = "vector.store",
        havingValue = "chroma"
)
public class ChromaService implements VectorStoreService{
    private static final String COLLECTIONS =
            "/api/v2/tenants/{tenant}/databases/{database}/collections";
    private static final String COLLECTION =
            COLLECTIONS + "/{collection}";
    private static final String COLLECTION_BY_NAME =
            COLLECTIONS + "/{name}";

    private final RestClient restClient;
    private final String tenant;
    private final String database;
    private final String collectionName;
    private volatile String collectionId;

    public ChromaService(
            RestClient.Builder builder,
            @Value("${chroma.url}") String chromaUrl,
            @Value("${chroma.tenant}") String tenant,
            @Value("${chroma.database}") String database,
            @Value("${chroma.collection}") String collectionName) {

        this.restClient = builder.clone().baseUrl(chromaUrl).build();
        this.tenant = tenant;
        this.database = database;
        this.collectionName = collectionName;
    }

    public void store(
            String id,
            String document,
            List<Double> embedding,
            String source) {

        Map<String, Object> request = Map.of(
                "ids", List.of(id),
                "documents", List.of(document),
                "embeddings", List.of(embedding),
                "metadatas", List.of(
                        Map.of(
                                "source", source
                        )
                )
        );

        restClient.post()
                .uri(COLLECTION + "/upsert", tenant, database, collectionId())
                .header("Content-Type", "application/json")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public List<String> search(List<Double> queryEmbedding, int topK) {
        Map<String, Object> request = Map.of(
                "query_embeddings", List.of(queryEmbedding),
                "n_results", topK
        );

        ChromaSearchResponse response = restClient.post()
                .uri(COLLECTION + "/query", tenant, database, collectionId())
                .header("Content-Type", "application/json")
                .body(request)
                .retrieve()
                .body(ChromaSearchResponse.class);

        if (response == null || response.documents() == null || response.documents().isEmpty()) {
            return List.of();
        }

        List<String> documents = response.documents().get(0);
        return documents == null ? List.of() : documents;
    }

    public void deleteBySource(String source) {
        Map<String, Object> request = Map.of(
                "where", Map.of(
                        "source", source
                )
        );

        restClient.post()
                .uri(COLLECTION + "/delete", tenant, database, collectionId())
                .header("Content-Type", "application/json")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private String collectionId() {
        String id = collectionId;
        if (id == null) {
            synchronized (this) {
                id = collectionId;
                if (id == null) {
                    collectionId = id = getOrCreateCollection();
                }
            }
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    private String getOrCreateCollection() {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(COLLECTION_BY_NAME, tenant, database, collectionName)
                    .retrieve()
                    .body(Map.class);

            return requireCollectionId(response);
        } catch (HttpClientErrorException.NotFound e) {
            Map<String, Object> request = Map.of("name", collectionName);

            Map<String, Object> response = restClient.post()
                    .uri(COLLECTIONS, tenant, database)
                    .header("Content-Type", "application/json")
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            return requireCollectionId(response);
        }
    }

    private static String requireCollectionId(Map<String, Object> response) {
        if (response == null || response.get("id") == null) {
            throw new IllegalStateException("Chroma did not return a collection id");
        }
        return String.valueOf(response.get("id"));
    }
}
