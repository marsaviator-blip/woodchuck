package org.woodchuck.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.woodchuck.pipeline.GraphEnhancementStrategy;

import ai.docling.serve.api.chunk.response.ChunkDocumentResponse;

@Service
// Activates when running the normal team environment, or when explicitly testing cloud embeddings
@Profile({"opensearch"}) 
public class OpensearchGraphEnhancementStrategy implements GraphEnhancementStrategy {

    @Override
    public void enhanceGraphTopology(String documentId, ChunkDocumentResponse response) {
        // Implement the logic to enhance the graph topology in OpenSearch based on the documentId and response
        // This could involve creating nodes, relationships, or updating existing nodes in the graph
        System.out.println("Enhancing graph topology for document ID: " + documentId);
        // Add your OpenSearch-specific enhancement logic here
    }

    @Override
    public boolean supportsGraph() {
        // Return true if this strategy supports graph enhancement, false otherwise
        return false; // Assuming this strategy supports graph enhancement
    }

}
