package org.woodchuck.zChecker.dtos;

import java.util.List;

/**
 * Data Transfer Object for transferring flat scholarly graph data 
 * with pre-aggregated relationships to the Vue 3 frontend.
 */
public class SearchResultDTO {
    private String id;
    private String type; // paper, author, institution
    private String title; // Represents paper title, author name, or institution name
    private List<String> authors;
    private List<String> institutions;
    private List<String> papers;

    // Default constructor required for serialization frameworks
    public SearchResultDTO() {
    }

    // Fully-parameterized constructor used by Neo4j mapping frameworks
    public SearchResultDTO(String id, String type, String title, 
                           List<String> authors, List<String> institutions, List<String> papers) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.authors = authors;
        this.institutions = institutions;
        this.papers = papers;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> authors) { this.authors = authors; }

    public List<String> getInstitutions() { return institutions; }
    public void setInstitutions(List<String> institutions) { this.institutions = institutions; }

    public List<String> getPapers() { return papers; }
    public void setPapers(List<String> papers) { this.papers = papers; }
}
