package org.woodchuck.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    // @PostMapping("/search-pgStore")
    // public ResponseEntity<List<Document>> search(@RequestBody request) {
    //     String question = "What are the subjects/topics in this model?";
    //     List<Document> results = searchVectorStoreService.search(request, question);
    //     return ResponseEntity.ok(results);
    // }
}