package org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cognitive_nodes")
public class CognitiveNode {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "focus_area", length = 100)
    private String focusArea;

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String subject;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cognitive_node_topics", joinColumns = @JoinColumn(name = "node_id"))
    @Column(name = "topic_name", length = 100)
    private Set<String> topics = new HashSet<>();

    @Column(name = "minio_payload_key", nullable = false, length = 512)
    private String minioPayloadKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Standard Null Constructor (Required by JPA)
    public CognitiveNode() {}

    // Clean, standard Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFocusArea() { return focusArea; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Set<String> getTopics() { return topics; }
    public void setTopics(Set<String> topics) { this.topics = topics; }

    public String getMinioPayloadKey() { return minioPayloadKey; }
    public void setMinioPayloadKey(String minioPayloadKey) { this.minioPayloadKey = minioPayloadKey; }

    public Instant getCreatedAt() { return createdAt; }

    // Modern Idiomatic equals/hashCode (Uses Object.hash - completely safe for JPA)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CognitiveNode that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
