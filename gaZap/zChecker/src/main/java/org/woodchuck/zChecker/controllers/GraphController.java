package org.woodchuck.zChecker.controllers;

import org.woodchuck.zChecker.dtos.GraphGistDTO;
import org.woodchuck.zChecker.repository.GraphRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graph")
@CrossOrigin(origins = "http://localhost:5173") // Adjust port to match your local Vue development server
public class GraphController {

    private final GraphRepository graphRepository;

    public GraphController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    @GetMapping("/gist")
    public GraphGistDTO getGraphGist() {
        Map<String, Object> stats = graphRepository.getMetaStats();
        
        long nodes = ((Number) stats.get("nodeCount")).longValue();
        long edges = ((Number) stats.get("relCount")).longValue();
        @SuppressWarnings("unchecked")
        Map<String, Long> labels = (Map<String, Long>) stats.get("labels");

        return new GraphGistDTO(nodes, edges, labels);
    }

    @GetMapping("/authors/search")
    public List<Map<String, Object>> searchAuthors(@RequestParam String query) {
        return graphRepository.searchAuthorsAutocomplete(query);
    }
}

