package org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "cognitive_morphisms")
public class CognitiveMorphism {

    @Id
    @Column(length = 36)
    private String id; // UUID String representation

    @Column(name = "source_node_id", nullable = false, length = 36)
    private String sourceNodeId;

    @Column(name = "target_node_id", nullable = false, length = 36)
    private String targetNodeId;

    /**
     * Identifies the category transformation type.
     * Examples: 'CROSS_SESSION_ADJACENCY', 'CHRONOLOGICAL_STEP', 'REFINEMENT'
     */
    @Column(name = "transition_type", nullable = false, length = 50)
    private String transitionType;

    /**
     * Calculated value (ranging from 0.0 to 1.0) indicating 
     * the semantic or categorical density of the connection.
     */
    @Column(name = "algebraic_weight", nullable = false)
    private double algebraicWeight;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Standard Null Constructor (Required by JPA/Hibernate specification)
    public CognitiveMorphism() {}

    // Complete Parameterized Constructor for custom setup pipelines
    public CognitiveMorphism(String id, String sourceNodeId, String targetNodeId, String transitionType, double algebraicWeight) {
        this.id = id;
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.transitionType = transitionType;
        this.algebraicWeight = algebraicWeight;
    }

    // Standard Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSourceNodeId() { return sourceNodeId; }
    public void setSourceNodeId(String sourceNodeId) { this.sourceNodeId = sourceNodeId; }

    public String getTargetNodeId() { return targetNodeId; }
    public void setTargetNodeId(String targetNodeId) { this.targetNodeId = targetNodeId; }

    public String getTransitionType() { return transitionType; }
    public void setTransitionType(String transitionType) { this.transitionType = transitionType; }

    public double getAlgebraicWeight() { return algebraicWeight; }
    public void setAlgebraicWeight(double algebraicWeight) { this.algebraicWeight = algebraicWeight; }

    public Instant getCreatedAt() { return createdAt; }

    // Idiomatic equals/hashCode based strictly on identity reference to prevent proxy leakage
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CognitiveMorphism that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
