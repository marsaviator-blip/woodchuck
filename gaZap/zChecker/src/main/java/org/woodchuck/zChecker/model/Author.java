package org.woodchuck.zChecker.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Author")
public class Author {
    @Id @GeneratedValue
    private String id;
    
    @Property("name")
    private String name;

    // Getters, Setters, and Constructors
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

