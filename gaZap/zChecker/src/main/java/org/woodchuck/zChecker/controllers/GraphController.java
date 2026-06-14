package org.woodchuck.zChecker.controllers;

import org.woodchuck.zChecker.dtos.GraphGistDTO;
import org.woodchuck.zChecker.repository.GraphRepository;
import org.woodchuck.zChecker.services.GraphMetricsService;
import org.woodchuck.zChecker.dtos.GraphStats;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/graph")
@CrossOrigin(origins = "https://localhost:3002") // Adjust port to match your local Vue development server
public class GraphController {

    private final GraphMetricsService graphMetricsService;

    // Spring automatically injects the service bean here
    public GraphController(GraphMetricsService graphMetricsService) {
        this.graphMetricsService = graphMetricsService;
    }

    @GetMapping("/counts")
    public ResponseEntity<GraphStats> getGraphCounts() {
        GraphStats stats = graphMetricsService.getDatabaseCounts();
        return ResponseEntity.ok(stats);
    }

    // private final GraphRepository graphRepository;

    // public GraphController(GraphRepository graphRepository) {
    //     this.graphRepository = graphRepository;
    // }

    // @GetMapping("/gist")
    // public GraphGistDTO getGraphGist() {
    //     Map<String, Object> stats = graphRepository.getMetaStats();
        
    //     long nodes = ((Number) stats.get("nodeCount")).longValue();
    //     long edges = ((Number) stats.get("relCount")).longValue();
    //     @SuppressWarnings("unchecked")
    //     Map<String, Long> labels = (Map<String, Long>) stats.get("labels");

    //     return new GraphGistDTO(nodes, edges, labels);
    // }

    // @GetMapping("/authors/search")
    // public List<Map<String, Object>> searchAuthors(@RequestParam String query) {
    //     return graphRepository.searchAuthorsAutocomplete(query);
    // }

}

