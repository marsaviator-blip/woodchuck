package org.woodchuck.entities;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Node
public class DocumentRelations {

        // ==========================================
    // 1. Core Spring AI / Docling Unified Fields
    // ==========================================

    @Id
    private String id; // Composite key: "documentId#chunkIndex" or Docling's UUID

    private String text; // The markdown/plain text content of the chunk

    @Property("embedding")
    private float[] embedding; // The 768 float array from nomic-embed-text

    // ==========================================
    // 2. Docling Layout Metadata Fields
    // ==========================================

    private String type; // Docling chunk types: "text", "heading", "table", "item"

    private Integer headingLevel; // NEW: 1, 2, 3 for hierarchy parsing

    @Property("table_html")
    private String tableHtml; // NEW: Stores raw HTML/JSON representation of tables

    @Property("references")
    private List<String> references; // NEW: Stores citation strings found in text

    @Property("page_numbers")
    private List<Integer> pageNumbers; // Which physical pages this chunk spans

    @Property("heading_path")
    private List<String> headingPath; // Array representing the breadcrumb header path

    @Property("image_description")
    private String imageDescription;

    @Property("image_class")
    private String imageClass;

    // ==========================================
    // 3. Advanced GraphRAG Structural Edges
    // ==========================================

    /**
     * Hierarchical Tree Relationship (Parent Section -> Child Chunk).
     * Connects granular text chunks back to their high-level structural "heading" parent node.
     */
    @Relationship(type = "HAS_CHILD", direction = Relationship.Direction.OUTGOING)
    private DocumentRelations parentSection;

    /**
     * Chronological Chain Relationship (Chunk N -> Chunk N+1).
     * Connects sequential segments in order to preserve the reading narrative flow across splits.
     */
    @Relationship(type = "NEXT_CHUNK", direction = Relationship.Direction.OUTGOING)
    private DocumentRelations nextChunk;

    /**
     * Structural Table Relationship (Table Node -> TableRow Entities).
     * Links a parent table node to individual structured row items, preventing vector contamination.
     */
    @Relationship(type = "REPRESENTS_ROW", direction = Relationship.Direction.OUTGOING)
    private Set<TableRowEntity> tableRows = new HashSet<>();

    // ==========================================
    // Constructors & Boilerplate
    // ==========================================

    public DocumentRelations() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getHeadingLevel() { return headingLevel; }
    public void setHeadingLevel(Integer level) { this.headingLevel = level; }
    
    public String getTableHtml() { return tableHtml; }
    public void setTableHtml(String tableHtml) { this.tableHtml = tableHtml; }
    
    public List<String> getReferences() { return references; }
    public void setReferences(List<String> references) { this.references = references; }
    public List<Integer> getPageNumbers() { return pageNumbers; }
    public void setPageNumbers(List<Integer> pages) { this.pageNumbers = pages; }
    public List<String> getHeadingPath() { return headingPath; }
    public void setHeadingPath(List<String> path) { this.headingPath = path; }
    public String getImageDescription() { return imageDescription; }
    public void setImageDescription(String imageDescription) { this.imageDescription = imageDescription; }

    public String getImageClass() { return imageClass; }
    public void setImageClass(String imageClass) { this.imageClass = imageClass; }
    public DocumentRelations getParentSection() { return parentSection; }
    public void setParentSection(DocumentRelations parent) { this.parentSection = parent; }
    public DocumentRelations getNextChunk() { return nextChunk; }
    public void setNextChunk(DocumentRelations next) { this.nextChunk = next; }
    public Set<TableRowEntity> getTableRows() { return tableRows; }
    public void setTableRows(Set<TableRowEntity> rows) { this.tableRows = rows; }

}

