package org.woodchuck.zChecker.dtos;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("DocumentNode") // Neo4j Label
public class DocumentsInNeo4j {

    @Id
    private String documentId;

    @Property("title")
    private String title;

    @Property("createdAt")
    private String createdAt;

    @Property("fileSize")
    private Long fileSize;

    // Constructors, Getters
    public DocumentsInNeo4j() {
    }   
    public DocumentsInNeo4j(String documentId, String title, String createdAt, Long fileSize) {
        this.documentId = documentId;
        this.title = title;
        this.createdAt = createdAt;
        this.fileSize = fileSize;
    }   
    public String getDocumentId() {
        return documentId;
    }
    public String getTitle() {
        return title;   
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public Long getFileSize() {
        return fileSize;   
    }
}

