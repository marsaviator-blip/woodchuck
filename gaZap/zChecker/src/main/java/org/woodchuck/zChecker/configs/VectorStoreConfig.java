package org.woodchuck.zChecker.configs;

import org.neo4j.driver.Driver;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
//import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore.Neo4jVectorStoreConfig;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore.Neo4jDistanceType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore neo4jVectorStore(Driver driver, EmbeddingModel embeddingModel) {
       // Build the configuration (dimensions MUST match your EmbeddingModel, e.g., 1536 for OpenAI)
       return Neo4jVectorStore.builder(driver, embeddingModel)
                .databaseName("neo4j") // Optional, defaults to "neo4j"
                .distanceType(Neo4jDistanceType.COSINE) // Choose distance metric (COSINE, EUCLIDEAN, MANHATTAN)
                .embeddingDimension(1536) // Must match your embedding model's output dimension
                .label("Document") // Node label for storing vectors
                .embeddingProperty("embedding") // Node property name for the vector data
                .indexName("spring_ai_vector_index") // Name of the index to create on the embedding property
                .initializeSchema(true) // Automatically create constraints and indexes on startup
                .build();
    }
}

