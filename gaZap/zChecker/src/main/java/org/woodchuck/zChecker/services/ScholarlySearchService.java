package org.woodchuck.zChecker.services;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScholarlySearchService {

    private final VectorStore vectorStore;

    public ScholarlySearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Executes a similarity search restricted by specific funding and author metadata.
     */
    public List<Document> searchByFundingAndAuthor(String query, String fundingOrg, String authorName) {
        // FilterExpressionBuilder b = new FilterExpressionBuilder();

        // // Build a type-safe SQL-like abstract syntax tree for metadata filtering
        // Filter.Expression filterExpression = b.and(
        //     b.eq("fundingInstitutions", fundingOrg),
        //     b.eq("authors", authorName).build()
        // );
String filterText = String.format("fundingInstitutions == '%s' && authors == '%s'", fundingOrg, authorName);

        // Package the vector search query, similarity threshold, and metadata filters
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(5) // Limit to top 5 results
                .similarityThreshold(0.7) // Strictness of text matching
                //.filterExpression(filterExpression) 
                .filterExpression(filterText) 
                .build();

        // Returns matches from your Vector Store containing text chunks and matching metadata
        return vectorStore.similaritySearch(searchRequest);
    }
}

