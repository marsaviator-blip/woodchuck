package org.woodchuck.services;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class OllamaEmbeddingService {
    
    private final EmbeddingModel embeddingModel;

     public OllamaEmbeddingService(OllamaEmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public float[] getEmbeddings(String text) {
        // Simple call to generate embedding for a single string
        return embeddingModel.embed(text);
    }   
}
