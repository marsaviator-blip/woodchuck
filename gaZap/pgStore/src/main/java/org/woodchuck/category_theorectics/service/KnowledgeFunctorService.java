package org.woodchuck.category_theorectics.service;

import org.woodchuck.category_theorectics.model.CategoryObjectNode;
import org.woodchuck.category_theorectics.repository.CategoryNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class KnowledgeFunctorService {

    private final EmbeddingModel embeddingModel;
    private final JdbcTemplate jdbcTemplate;
    private final CategoryNodeRepository categoryNodeRepository;

    public KnowledgeFunctorService(EmbeddingModel embeddingModel, 
                                   JdbcTemplate jdbcTemplate, 
                                   CategoryNodeRepository categoryNodeRepository) {
        this.embeddingModel = embeddingModel;
        this.jdbcTemplate = jdbcTemplate;
        this.categoryNodeRepository = categoryNodeRepository;
    }

    @Transactional
    public void mapInformalNoteToFormalCategory(String rawNoteContent, String targetFormalPaperId) {
        String informalNodeId = UUID.randomUUID().toString();
 
        // this did not work and is inefficient
        // List<Double> embedding = embeddingModel.embed(rawNoteContent);
        // float[] vectorArray = new float[embedding.size()];
        // for (int i = 0; i < embedding.size(); i++) {
        //     vectorArray[i] = embedding.get(i).floatValue();
        // }

        // Option A: Clean stream mapping
        float[] vectorArray = embeddingModel.embed(rawNoteContent);
// float[] vectorArray = embedding.stream()
//     .mapToDouble(Double::doubleValue)
//     .collect(
//         () -> new float[embedding.size()],
//         (arr, val) -> arr[arr.length - 1] = (float) val, // Optimized by library utilities
//         (arr1, arr2) -> {}
//     );

    // was not availalbe 
// Option B: Better yet, check if your embeddingModel has a direct primitive method
// Many Spring AI models provide direct access to float[] vectors to bypass Double objects entirely
//float[] vectorArray = embeddingModel.embedToFloatArray(rawNoteContent); 


        String pgSql = "INSERT INTO informal_notes_vectors (id, content, embedding) VALUES (?, ?, ?::vector)";
        jdbcTemplate.update(pgSql, informalNodeId, rawNoteContent, vectorArray);

        CategoryObjectNode informalObj = new CategoryObjectNode(
            informalNodeId, "User Note", "INFORMAL", rawNoteContent.substring(0, Math.min(rawNoteContent.length(), 50))
        );
        categoryNodeRepository.save(informalObj);

        categoryNodeRepository.createFunctorMorphism(informalNodeId, targetFormalPaperId, "HIGH_SIMILARITY");
    }
}
