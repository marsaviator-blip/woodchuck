package org.woodchuck.zChecker.services;

import org.woodchuck.zChecker.dtos.SearchResultDTO;
import org.woodchuck.zChecker.repository.ScholarlyRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScholarlyService {

    private final ScholarlyRepository scholarlyRepository;

    public ScholarlyService(ScholarlyRepository scholarlyRepository) {
        this.scholarlyRepository = scholarlyRepository;
    }

    /**
     * Executes the graph search based on entity type and user query.
     * Maps requests directly to optimized Neo4j Cypher projections.
     */
    public List<SearchResultDTO> searchGraph(String query, String type, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }

        switch (type.toLowerCase()) {
            case "paper":
                return scholarlyRepository.searchPapers(query, pageable);
                
            case "author":
                return scholarlyRepository.searchAuthors(query, pageable);
                
            case "institution":
                // If you build an institution search query, route it here
                return new ArrayList<>(); 
                
            case "all":
                return searchAllCategories(query, pageable);
                
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Combines multiple entity types when searching the entire scholarly graph.
     * Distributes the pagination page size evenly across domains.
     */
    private List<SearchResultDTO> searchAllCategories(String query, Pageable pageable) {
        List<SearchResultDTO> combinedResults = new ArrayList<>();
        
        // Fetch up to the page limit for each category 
        List<SearchResultDTO> papers = scholarlyRepository.searchPapers(query, pageable);
        List<SearchResultDTO> authors = scholarlyRepository.searchAuthors(query, pageable);

        combinedResults.addAll(papers);
        combinedResults.addAll(authors);

        // Cap the total returned payload to your pageable limit to protect the Vue frontend
        if (combinedResults.size() > pageable.getPageSize()) {
            return combinedResults.subList(0, pageable.getPageSize());
        }

        return combinedResults;
    }
}


