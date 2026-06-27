package org.woodchuck.zChecker.controllers;

import org.woodchuck.zChecker.dtos.GraphGistDTO;
import org.woodchuck.zChecker.repository.GraphRepository;
import org.woodchuck.zChecker.services.GraphMetricsService;
import org.woodchuck.zChecker.services.GraphVisualService;
import org.woodchuck.zChecker.dtos.GraphStats;
import org.woodchuck.zChecker.dtos.NetworkGraphPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/graph")
@CrossOrigin(origins = "https://localhost:3002") // Adjust port to match your local Vue development server
public class GraphController {

    private final GraphMetricsService graphMetricsService;
    private final GraphVisualService graphVisualService;

    // Spring automatically injects the service bean here
    public GraphController(GraphMetricsService graphMetricsService, GraphVisualService graphVisualService) {
        this.graphMetricsService = graphMetricsService;
        this.graphVisualService = graphVisualService;
    }

    @GetMapping("/counts")
    public ResponseEntity<GraphStats> getGraphCounts() {
        GraphStats stats = graphMetricsService.getDatabaseCounts();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/topology")
    public NetworkGraphPayload getKnowledgeGraphTopology(@RequestParam(defaultValue = "150") int limit) {
        return graphVisualService.fetchSubGraphTopology(limit);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearDatabase() {
        try {
            // Controller asks the service to do the heavy lifting
            graphMetricsService.clearDatabase();
            return ResponseEntity.ok("Database wiped clean successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing request: " + e.getMessage());
        }
    }

}

