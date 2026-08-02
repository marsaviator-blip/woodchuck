package org.woodchuck.services;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import ai.docling.serve.api.chunk.response.ChunkDocumentResponse;
import org.woodchuck.pipeline.GraphEnhancementStrategy;

import org.springframework.data.neo4j.core.Neo4jClient; // Included natively in spring-ai-neo4j

@Service
// Activates when running the normal team environment, or when explicitly testing cloud embeddings
@Profile({"default", "embeddings"}) 
public class Neo4jGraphEnhancementStrategy implements GraphEnhancementStrategy {

    private final Neo4jClient neo4jClient;

    public Neo4jGraphEnhancementStrategy(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public void enhanceGraphTopology(String documentId, ChunkDocumentResponse response) {
        // Implement the logic to enhance the graph topology in Neo4j based on the documentId and response
        // This could involve creating nodes, relationships, or updating existing nodes in the graph
        System.out.println("Enhancing graph topology for document ID: " + documentId);
        String linkChronological = """
            MATCH (a:CustomDocument), (b:CustomDocument)
            WHERE a.`metadata.nextChunkId` = b.id
            MERGE (a)-[:NEXT]->(b)
            """;

        // 2. Updated Hierarchical Tree Stitching Query
        String linkHierarchical = """
            MATCH (child:CustomDocument), (parent:CustomDocument)
            WHERE child.`metadata.parentSectionId` IS NOT NULL
            AND child.`metadata.parentSectionId` = parent.id
            MERGE (parent)-[:HAS_CHILD]->(child)
            """;

        neo4jClient.query(linkChronological).run();
        neo4jClient.query(linkHierarchical).run();
    }

    @Override
    public boolean supportsGraph() {
        // Return true if this strategy supports graph enhancement, false otherwise
        return true; // Assuming this strategy supports graph enhancement
    }

}
