package org.woodchuck.configs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingResponseMetadata;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

// Import the official vendor SDK components used by Spring AI 2.x
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

@Configuration
@Profile("embeddings") // Only loads when running --spring.profiles.active=embeddings
public class NomicCloudConfig {

    @Value("${nomic.api.base-url}")
    private String baseUrl;

    @Value("${nomic.api.key}")
    private String apiKey;

    @Bean
    public EmbeddingModel embeddingModel() {
        RestClient client = RestClient.builder()
            .baseUrl(this.baseUrl)
            .defaultHeader("Authorization", "Bearer " + this.apiKey)
            .defaultHeader("Content-Type", "application/json")
            .build();

        return new EmbeddingModel() {
            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                List<String> texts = request.getInstructions();

                Map<String, Object> body = Map.of(
                    "texts", texts,
                    "model", "nomic-embed-text-v1.5",
                    "task_type", "search_document"
                );

                NomicResponse response = client.post()
                    .uri("/v1/embedding/text") // Explicit target endpoint matching documentation
                    .body(body)
                    .retrieve()
                    .body(NomicResponse.class);

                List<Embedding> embeddings = new ArrayList<>();
                if (response != null && response.embeddings() != null) {
                    for (int i = 0; i < response.embeddings().size(); i++) {
                        List<Double> rawVector = response.embeddings().get(i);
                        
                        // Convert List<Double> explicitly to the modern primitive float[] array
                        float[] floatVector = new float[rawVector.size()];
                        for (int j = 0; j < rawVector.size(); j++) {
                            floatVector[j] = rawVector.get(j).floatValue();
                        }
                        
                        embeddings.add(new Embedding(floatVector, i));
                    }
                }

                return new EmbeddingResponse(embeddings);
            }

            @Override
            public float[] embed(Document document) {
                // Uses the explicit modern float[] return requirement
                EmbeddingResponse response = call(new EmbeddingRequest(
                    List.of(document.getText()), 
                    EmbeddingOptions.builder().build()
                ));
                return response.getResults().get(0).getOutput();
            }
        };
    }

    private record NomicResponse(List<List<Double>> embeddings, String model) {}

}
