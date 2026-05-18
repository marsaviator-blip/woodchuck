package org.woodchuck.controllers;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;

import org.woodchuck.services.SearchVectorStoreService;
import java.util.List;



@RestController
@RequestMapping("/search-vector-store")
public class SearchVectorStoreController {    

    private final SearchVectorStoreService searchVectorStoreService;

    public SearchVectorStoreController(SearchVectorStoreService searchVectorStoreService) {
        this.searchVectorStoreService = searchVectorStoreService;
    }

    // need to add swagger.  need to pass the question in the request
    @PostMapping("/search-pgStore")
    @Operation(summary = "Ask a question of the pgvector store",
            description = "Can the model answer your question")
    public ResponseEntity<List<Document>> search(@RequestParam("question") @NotBlank String question) {
        String questionStart = "What are the subjects/topics in this model?";
        List<Document> results = searchVectorStoreService.search(question);
        return ResponseEntity.ok(results);
    }
}