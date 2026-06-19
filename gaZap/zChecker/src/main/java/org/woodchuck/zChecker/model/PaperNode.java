package org.woodchuck.zChecker.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.HashSet;
import java.util.Set;

@Node("Paper") // Maps this entity to a "Paper" node in a Graph DB
public class PaperNode {

    @Id
    private String id; // Often a DOI, UUID, or citation key

    @Property("title")
    private String title;

    @Property("abstractText")
    private String abstractText;

    @Property("publishYear")
    private Integer publishYear;

    // Relationship linking back to your Author entity
    @Relationship(type = "WRITTEN_BY", direction = Relationship.Direction.OUTGOING)
    private Set<Author> authors = new HashSet<>();

    // Self-referencing relationship to track citations or reference networks
    @Relationship(type = "CITES", direction = Relationship.Direction.OUTGOING)
    private Set<PaperNode> citations = new HashSet<>();

    // Standard Constructors
    public PaperNode() {}

    public PaperNode(String id, String title, Integer publishYear) {
        this.id = id;
        this.title = title;
        this.publishYear = publishYear;
    }

    // Getters and Setters...
}

