package org.woodchuck.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.UUID;

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
import ai.docling.serve.api.convert.request.source.Source;
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
                String documentId =  UUID.randomUUID().toString();
                if(!request.getSources().isEmpty()){
                    Source source = request.getSources().get(0);
                    if (source instanceof HttpSource httpSource) {
                        try {
                            java.net.URI url = httpSource.getUrl();
                            String path = url.getPath(); // Extracts everything after the domain (e.g., /dataset1/raw_study.pdf)
                            
                            if (path != null && !path.isEmpty()) {
                                // Extract the final segment after the last forward slash
                                String fileName = path.substring(path.lastIndexOf('/') + 1);
                                
                                if (!fileName.isEmpty()) {
                                    documentId = fileName; // Sets documentId to "raw_study.pdf"
                                } else {
                                    documentId = url.toString(); // Fallback if trailing slash found
                                }
                            } else {
                                documentId = url.toString(); // Fallback if path is completely empty
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to extract filename from URL, falling back to full string: " + e.getMessage());
                            documentId = httpSource.getUrl().toString();
                        }
                    }
                }
                System.out.println("Extracted Document ID: " + documentId);
                var doclingChunks = response.getChunks();
                if (doclingChunks == null || doclingChunks.isEmpty()) return response;

                List<DocumentRelations> instantiatedEntities = new ArrayList<>();
                // Caches our section header node references using the header title string as the key
                Map<String, DocumentRelations> headingNodeCache = new HashMap<>();

                for (int i = 0; i < doclingChunks.size(); i++) {
                    var chunk = doclingChunks.get(i);
                    
                    String textContent = chunk.getText(); 
                    if (textContent == null) textContent = chunk.toString();
                    
                    String compositeId = documentId + "#chunk-" + i;

                    DocumentRelations currentEntity = new DocumentRelations();
                    currentEntity.setId(compositeId);
                    currentEntity.setText(textContent);
                    
                    // Sync your verified array collections straight out of Docling 0.5.2
                    currentEntity.setPageNumbers((List<Integer>) chunk.getPageNumbers());
                    
                    List<String> headingsList = (List<String>) chunk.getHeadings();
                    currentEntity.setHeadingPath(headingsList);

                    // Baseline fallback defaults
                    currentEntity.setType("text");
                    boolean isHeaderNode = false;
                    String parentHeaderTitle = null;

                    if (headingsList != null && !headingsList.isEmpty()) {
                        // The last entry in the list is always the heading this content belongs to
                        parentHeaderTitle = headingsList.get(headingsList.size() - 1);
                        
                        // CRITICAL CHECK: If this is chunk-0 or has a short text payload that matches the header exactly,
                        // treat it as the master structural section title node itself.
                        String cleanText = textContent.trim().toLowerCase();
                        String cleanHeader = parentHeaderTitle.trim().toLowerCase();
                        
                        if (i == 0 || cleanText.equals(cleanHeader)) {
                            currentEntity.setType("heading");
                            isHeaderNode = true;
                            headingNodeCache.put(parentHeaderTitle, currentEntity);
                        }
                    }

                    // 1. Chronological Link: Track sequential linear reading stream order
                    if (i > 0) {
                        DocumentRelations previousEntity = instantiatedEntities.get(i - 1);
                        previousEntity.setNextChunk(currentEntity);
                    }

                    // 2. Hierarchical Link: Track structural HAS_CHILD parent-child trees
                    if (isHeaderNode) {
                        // If this is a nested sub-heading, look up one level higher in the array to connect it
                        if (headingsList.size() > 1) {
                            String superiorHeader = headingsList.get(headingsList.size() - 2);
                            if (headingNodeCache.containsKey(superiorHeader)) {
                                currentEntity.setParentSection(headingNodeCache.get(superiorHeader));
                            }
                        }
                    } else {
                        // This is a standard math equation, algorithm block, or paragraph text chunk.
                        // Look up the matching cached section header node to link it as a child.
                        if (parentHeaderTitle != null) {
                            // Safety Check: If the header node hasn't been instantiated yet (due to chunk merging),
                            // dynamically create a virtual section header node to anchor our children!
                            if (!headingNodeCache.containsKey(parentHeaderTitle)) {
                                DocumentRelations virtualHeader = new DocumentRelations();
                                virtualHeader.setId(documentId + "#header-" + parentHeaderTitle.hashCode());
                                virtualHeader.setText(parentHeaderTitle);
                                virtualHeader.setType("heading");
                                
                                headingNodeCache.put(parentHeaderTitle, virtualHeader);
                                instantiatedEntities.add(virtualHeader);
                            }
                            
                            // Connect the text chunk to its section parent
                            currentEntity.setParentSection(headingNodeCache.get(parentHeaderTitle));
                        }
                    }

                    instantiatedEntities.add(currentEntity);
                }                    

                // Save the rich multi-relational tree hierarchy down into Neo4j
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
                    // System.out.println("Adding " + chunks.size() + " chunks to the vector store.");
                    // vectorStore.add(chunks);
                    // System.out.println("Chunks added to vector store successfully.");
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
