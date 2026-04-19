package org.hatchery.services;

import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class TextEmbeddingService {

    private final EmbeddingModel embeddingModel;

    public TextEmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<float[]> getEmbeddings(List<String> textList) {
        return embeddingModel.embed(textList);
    }
 }
