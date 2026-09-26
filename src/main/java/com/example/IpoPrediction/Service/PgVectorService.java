package com.example.IpoPrediction.Service;

import com.pgvector.PGvector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(
        name = "vector.store",
        havingValue = "pgvector"
)
public class PgVectorService implements VectorStoreService{

    private final JdbcTemplate jdbcTemplate;

    public PgVectorService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void store(
            String id,
            String document,
            List<Double> embedding,
            String source) {

        float[] vector = toFloatArray(embedding);

        String sql = """
                INSERT INTO ipo_documents
                    (id, document, embedding, source)
                VALUES
                    (?, ?, ?, ?)
                ON CONFLICT (id)
                DO UPDATE SET
                    document = EXCLUDED.document,
                    embedding = EXCLUDED.embedding,
                    source = EXCLUDED.source
                """;

        jdbcTemplate.update(
                sql,
                id,
                document,
                new PGvector(vector),
                source
        );
    }

    public List<String> search(
            List<Double> queryEmbedding,
            int topK) {

        float[] vector = toFloatArray(queryEmbedding);

        String sql = """
                SELECT document
                FROM ipo_documents
                ORDER BY embedding <=> ?
                LIMIT ?
                """;

        return jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setObject(1, new PGvector(vector));
                    ps.setInt(2, topK);
                },
                (rs, rowNum) -> rs.getString("document")
        );
    }

    public void deleteBySource(String source) {

        String sql = """
                DELETE FROM ipo_documents
                WHERE source = ?
                """;

        jdbcTemplate.update(sql, source);
    }

    private float[] toFloatArray(List<Double> embedding) {

        float[] vector = new float[embedding.size()];

        for (int i = 0; i < embedding.size(); i++) {
            vector[i] = embedding.get(i).floatValue();
        }

        return vector;
    }
}
