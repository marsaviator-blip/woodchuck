package org.woodchuck.category_theorectics.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class FunctorMapping {
    @Id @GeneratedValue
    private Long id;
    
    private String mappingStrength; // e.g., "HIGH_SIMILARITY", "EXACT_PROOF"
    
    @TargetNode
    private CategoryObjectNode targetFormalObject;

    public FunctorMapping(String mappingStrength, CategoryObjectNode targetFormalObject) {
        this.mappingStrength = mappingStrength;
        this.targetFormalObject = targetFormalObject;
    }
    // Getters and Setters
}
