package org.woodchuck.services;

import java.util.List;
import org.springframework.stereotype.Service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;  
import org.springframework.ai.vectorstore.SearchRequest;

@Service
public class SearchVectorStoreService {

    private final VectorStore vectorStore;
    
    public SearchVectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore; 
    }

    public List<Document> search(String question) {
        List<Document> results = vectorStore.similaritySearch(
        SearchRequest.builder()
            .query(question)
            .topK(3) // Number of results to return
            .similarityThreshold(0.7) // Only return results with >70% similarity
            .build()
        );
        return results;
    }  
}
