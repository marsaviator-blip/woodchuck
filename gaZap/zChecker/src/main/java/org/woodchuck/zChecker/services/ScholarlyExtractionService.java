package org.woodchuck.zChecker.services;    

import org.woodchuck.zChecker.controllers.ScholarlyMetadata;

import io.arconia.ai.document.docling.DoclingDocumentReader;
import ai.docling.serve.api.DoclingServeApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class ScholarlyExtractionService {

    private final ChatClient chatClient;
    private final DoclingServeApi doclingServeApi;

    // Inject ChatClient.Builder for Spring AI 2.0 best practices
    public ScholarlyExtractionService(ChatClient.Builder chatClientBuilder, DoclingServeApi doclingServeApi) {
        this.chatClient = chatClientBuilder.build();
        this.doclingServeApi = doclingServeApi;
    }

//     public List<Document> parseWithSpringAi(DoclingServeApi doclingServeApi) {
//         Resource pdfFile = new ClassPathResource("document.pdf");

//         // Arconia maps the Docling server output directly to Spring AI document objects
//         return DoclingDocumentReader.builder()
//                 .doclingServeApi(doclingServeApi)
//                 .files(pdfFile)
//                 .build()
//                 .get();
//     }

    public ScholarlyMetadata extractPaperMetadata(Resource pdfResource) {
        // 1. Structural extraction via Docling
        // List<Document> doclingParsedDocs = DoclingDocumentReader.builder()
        //         .doclingServeApi(doclingServeApi)
        //         .files(pdfResource)
        //         .build()
        //         .get();
List<Document> doclingParsedDocs = Collections.emptyList(); // Placeholder for actual Docling parsing
        // 2. Safely merge extracted segments into single context
        String paperText = doclingParsedDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        // 3. Request type-safe schema mapping via ChatClient fluent API
        return this.chatClient.prompt()
                // .user(u -> u.text("""
                //         You are a strict academic data extractor. 
                //         Analyze the following text parsed by Docling and extract the requested fields.
                        
                //         --- Document Text ---
                //         {text}
                //         """)
                //         .param("text", paperText))
                // // Direct Jackson 3 schema-enforcement mapping
                // .entity(ScholarlyMetadata.class) 
                // .call()
                // .entity();
        // Pass the raw string text directly to the user method
        .user("""
                You are a strict academic data extractor. 
                Analyze the following text parsed by Docling and extract the requested fields.
                
                --- Document Text ---
                {text}
                """)
        // Supply your parameters directly on the top-level request specification
        .system(s -> s.param("text", paperText)) 
        .call()
        .entity(ScholarlyMetadata.class);
    }
}

