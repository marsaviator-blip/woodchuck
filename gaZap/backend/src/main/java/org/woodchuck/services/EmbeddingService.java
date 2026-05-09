package org.woodchuck.services;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public float[] getEmbeddings(String text) {
        // Simple call to generate embedding for a single string
        return embeddingModel.embed(text);
    }

    public EmbeddingResponse getDetailedResponse(List<String> texts) {
        // Batch processing with full response metadata
        return embeddingModel.call(new EmbeddingRequest(texts, OllamaEmbeddingOptions.builder().build()));
    }
}
