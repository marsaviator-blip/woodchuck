package org.woodchuck.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import org.woodchuck.repositories.DocumentGraphRepository;
import org.woodchuck.entities.DocumentRelations;
import org.woodchuck.entities.TableRowEntity;

import ai.docling.serve.api.DoclingServeApi;
import ai.docling.serve.api.chunk.request.HybridChunkDocumentRequest;
import ai.docling.serve.api.chunk.request.options.ChunkerOptions;
import ai.docling.serve.api.chunk.request.options.HybridChunkerOptions;
import ai.docling.serve.api.chunk.response.ChunkDocumentResponse;
import ai.docling.serve.api.convert.request.ConvertDocumentRequest;
import ai.docling.serve.api.convert.request.source.HttpSource;
import ai.docling.serve.api.convert.response.ConvertDocumentResponse;
import ai.docling.serve.api.convert.response.InBodyConvertDocumentResponse;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class DoclingAsyncService {

    @Value("${arconia.docling.polling.delay-seconds:2}")
    private int pollDelaySeconds;

    @Value("${arconia.docling.polling.max-attempts:60}")
    private int maxAttempts;

    private final DoclingServeApi doclingServeApi;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final VectorStore vectorStore;
    private final DocumentGraphRepository documentGraphRepository;

    public DoclingAsyncService(DoclingServeApi doclingServeApi, VectorStore vectorStore, DocumentGraphRepository documentGraphRepository) {
        this.doclingServeApi = doclingServeApi;
        this.vectorStore = vectorStore;
        this.documentGraphRepository = documentGraphRepository;
    }

    public CompletableFuture<ChunkDocumentResponse> processDocumentAsync(HybridChunkDocumentRequest request) {
        CompletionStage<ChunkDocumentResponse> stage = doclingServeApi.chunkSourceWithHybridChunkerAsync(request);
        CompletableFuture<ChunkDocumentResponse> resultFuture =stage.toCompletableFuture().thenApply(response -> {
                System.out.println("Document conversion succeeded: ");
                String documentId =  "the document";//request.getSources().get(0).getUrl().toString(); // Using the URL as the document ID
                    var doclingChunks = response.getChunks();
                    if (doclingChunks == null || doclingChunks.isEmpty()) return response; // No chunks to process
                    List<DocumentRelations> instantiatedEntities = new ArrayList<>();
                    Map<String, DocumentRelations> lookupTable = new HashMap<>();
                    for (int i = 0; i < doclingChunks.size(); i++) {
                        // HERE is where doclingChunks is used to feed the pipeline!
                        var chunk = doclingChunks.get(i);
                        
                        // Extract the text and layout type from the Docling chunk object
                        String textContent = chunk.toString(); // or chunk.getText()
                        String chunkType = "text";             // default placeholder (e.g., chunk.getType())
                        
                        String compositeId = documentId + "#chunk-" + i;

                        // Instantiate the graph node
                        DocumentRelations entity = new DocumentRelations();
                        entity.setId(compositeId);
                        entity.setText(textContent);
                        entity.setType(chunkType); 

                        // Generate the semantic vector using your local Ollama container
                        // float[] vector = embeddingModel.embed(textContent);
                        // entity.setEmbedding(vector);

                        // Collect them into our tracking arrays for Step 2
                        instantiatedEntities.add(entity);
                        lookupTable.put(compositeId, entity);
                    }                    
                    for (int i = 0; i < instantiatedEntities.size(); i++) {
                        DocumentRelations current = instantiatedEntities.get(i);
                        if (i < instantiatedEntities.size() - 1) {
                            current.setNextChunk(instantiatedEntities.get(i + 1));
                        }

                        // B. Document Hierarchy Tree Connection: (Header Section)-[:HAS_CHILD]->(Text Paragraph)
                        // If docling specifies an explicit parent index anchor, link them:
                        String structuralParentId = documentId + "#chunk-" + (i - 1); // Mock example logic
                        if (lookupTable.containsKey(structuralParentId) && i > 0) {
                            DocumentRelations sectionHeaderNode = lookupTable.get(structuralParentId);
                            if ("heading".equalsIgnoreCase(sectionHeaderNode.getType())) {
                                current.setParentSection(sectionHeaderNode);
                            }
                        }
                    }
                    documentGraphRepository.saveAll(instantiatedEntities);

                    List<Document> chunks = instantiatedEntities.stream()
                            .map(graphNode -> {
                                // 1. Maintain layout metadata using a flat key map
                                Map<String, Object> metadata = new HashMap<>();
                                
                                // Map rich data from your entity class fields natively
                                if (graphNode.getType() != null) {
                                    metadata.put("type", graphNode.getType());
                                }
                                if (graphNode.getHeadingPath() != null) {
                                    metadata.put("headings", graphNode.getHeadingPath());
                                }
                                if (graphNode.getPageNumbers() != null) {
                                    metadata.put("page_numbers", graphNode.getPageNumbers());
                                }

                                // 2. CRITICAL: Use the 3-argument constructor to pass your exact Neo4j ID
                                // Document(String id, String content, Map<String, Object> metadata)
                                return new Document(
                                    graphNode.getId(),    // Explicitly syncs Neo4j Key to Vector Store Key!
                                    graphNode.getText(),  // Content segment
                                    metadata              // Filtering payload map
                                );
                            })
                            .toList();
                    System.out.println("Adding " + chunks.size() + " chunks to the vector store.");
                    vectorStore.add(chunks);
                    System.out.println("Chunks added to vector store successfully.");
                    return response;
        }).exceptionally(throwable -> {
            // Only runs if there was an error
            System.err.println("Document conversion failed: " + throwable.getMessage());
            //log.error("Something went wrong", throwable);
            return null; 
        });         
        return resultFuture;
    }

    private void pollConvertSource(ConvertDocumentRequest request,
                                   CompletableFuture<ConvertDocumentResponse> resultFuture,
                                   int attempt) {
        if (attempt >= maxAttempts) {
            resultFuture.completeExceptionally(new TimeoutException(
                    "Docling conversion timed out after " + (pollDelaySeconds * maxAttempts) + " seconds"));
            return;
        }

        scheduler.schedule(() -> {
            try {
                ConvertDocumentResponse response = doclingServeApi.convertSource(request);
                resultFuture.complete(response);
            } catch (Exception e) {
                if (isTaskResultPending(e)) {
                    pollConvertSource(request, resultFuture, attempt + 1);
                } else {
                    resultFuture.completeExceptionally(e);
                }
            }
        }, attempt == 0 ? 0 : pollDelaySeconds, TimeUnit.SECONDS);
    }

    private boolean isTaskResultPending(Throwable throwable) {
        if (throwable instanceof HttpClientErrorException httpError) {
            return httpError.getStatusCode() == HttpStatus.NOT_FOUND &&
                    httpError.getResponseBodyAsString().contains("Task result not found");
        }
        return false;
    }
}
