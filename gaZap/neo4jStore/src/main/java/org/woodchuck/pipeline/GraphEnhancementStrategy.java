package org.woodchuck.pipeline;

import ai.docling.serve.api.chunk.response.ChunkDocumentResponse;

public interface GraphEnhancementStrategy {
    // Defines what actions to take after standard vector storage completes
    void enhanceGraphTopology(String documentId, ChunkDocumentResponse response);
    
    // Allows the service layer to check if the active profile supports graph actions
    boolean supportsGraph();
}
