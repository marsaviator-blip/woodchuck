package org.woodchuck.category_theorectics.controller; 

import org.woodchuck.category_theorectics.service.KnowledgeFunctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/knowledge")
@CrossOrigin(origins = "http://localhost:4200") // Allows local Angular dev server
public class KnowledgeFunctorController {

    private final KnowledgeFunctorService functorService;

    public KnowledgeFunctorController(KnowledgeFunctorService functorService) {
        this.functorService = functorService;
    }

    @PostMapping("/functor-map")
    public ResponseEntity<Map<String, String>> createMapping(@RequestBody Map<String, String> requestPayload) {
        String content = requestPayload.get("content");
        String formalPaperId = requestPayload.get("formalPaperId");

        if (content == null || formalPaperId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing content or formalPaperId"));
        }

        // Execute the pipeline: Vectorize -> pgvector -> Neo4j Objects -> Morphism Link
        functorService.mapInformalNoteToFormalCategory(content, formalPaperId);

        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Morphism mapped successfully"));
    }
}
