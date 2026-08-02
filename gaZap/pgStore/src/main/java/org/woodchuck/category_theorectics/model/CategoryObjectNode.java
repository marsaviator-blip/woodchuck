package org.woodchuck.category_theorectics.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("CategoryObject")
public class CategoryObjectNode {
    @Id
    private String id; // Document Chunk ID or Note ID
    private String label;
    private String categoryType; // "INFORMAL" or "FORMAL"
    private String contentSummary;

    public CategoryObjectNode(String id, String label, String categoryType, String contentSummary) {
        this.id = id;
        this.label = label;
        this.categoryType = categoryType;
        this.contentSummary = contentSummary;
    }
    // Getters and Setters
}
