package org.woodchuck.zChecker.controllers;

import org.woodchuck.zChecker.services.ScholarlyRagService; 

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@Tag(name = "Scholarly RAG Operations", description = "Endpoints for context-constrained LLM Generation")
public class ScholarlyRagController {

    private final ScholarlyRagService ragService;

    public ScholarlyRagController(ScholarlyRagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/ask")
    @Operation(summary = "Ask a research question restricted by metadata", 
               description = "Queries the vector store for semantic matches and forces the LLM to synthesize an answer using only contexts that fit the specified author and funding agency parameters.")
    public String askPaper(
            @Parameter(description = "The research question or topic to investigate", example = "What were the key discoveries regarding memory storage?")
            @RequestParam String question,
            
            @Parameter(description = "Filter exclusively by this funding institution", example = "NSF")
            @RequestParam String funding,
            
            @Parameter(description = "Filter exclusively by this author's name", example = "Dr. Smith")
            @RequestParam String author) {
        return ragService.answerWithFilters(question, funding, author);
    }
}

