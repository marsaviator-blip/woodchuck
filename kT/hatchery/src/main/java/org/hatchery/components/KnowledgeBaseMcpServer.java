package org.hatchery.components;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
//import org.jsoup.nodes.Document;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeBaseMcpServer {

    private final VectorStore vectorStore;

    public KnowledgeBaseMcpServer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @McpTool(name = "search_knowledge_base", description = "Searches internal documents for relevant information")
    public List<String> search(String query) {
        // Perform similarity search
        List<Document> results = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(query)
                .topK(3)
                .build()
        );

        return results.stream().map(Document::getFormattedContent).toList();
    }
}

