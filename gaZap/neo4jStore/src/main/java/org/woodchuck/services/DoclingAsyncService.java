package org.woodchuck.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.UUID;

import ai.docling.core.DoclingDocument;
import ai.docling.serve.api.DoclingServeApi;
import ai.docling.serve.api.chunk.request.HybridChunkDocumentRequest;
import ai.docling.serve.api.chunk.request.options.ChunkerOptions;
import ai.docling.serve.api.chunk.request.options.HybridChunkerOptions;
import ai.docling.serve.api.chunk.response.ChunkDocumentResponse;
import ai.docling.serve.api.chunk.response.ExportDocumentResponse;
import ai.docling.serve.api.convert.request.ConvertDocumentRequest;
import ai.docling.serve.api.convert.request.source.HttpSource;
import ai.docling.serve.api.convert.request.source.Source;
import ai.docling.serve.api.convert.response.ConvertDocumentResponse;
import ai.docling.serve.api.convert.response.InBodyConvertDocumentResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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

import org.neo4j.driver.Driver;
import org.springframework.data.neo4j.core.Neo4jClient; // Included natively in spring-ai-neo4j

@Service
public class DoclingAsyncService {

    @Value("${arconia.docling.polling.delay-seconds:2}")
    private int pollDelaySeconds;

    @Value("${arconia.docling.polling.max-attempts:60}")
    private int maxAttempts;

    private final DoclingServeApi doclingServeApi;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final VectorStore vectorStore;
    private final Neo4jClient neo4jClient;

    public DoclingAsyncService(DoclingServeApi doclingServeApi, VectorStore vectorStore, Neo4jClient neo4jClient) {
        this.doclingServeApi = doclingServeApi;
        this.vectorStore = vectorStore;
        this.neo4jClient = neo4jClient;
    }

    public CompletableFuture<ChunkDocumentResponse> processDocumentAsync(HybridChunkDocumentRequest request) {
        CompletionStage<ChunkDocumentResponse> stage = doclingServeApi.chunkSourceWithHybridChunkerAsync(request);
        CompletableFuture<ChunkDocumentResponse> resultFuture =stage.toCompletableFuture().thenApply(response -> {
                System.out.println("Document conversion succeeded: "+response.getDocuments().size());
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
                var documents = response.getDocuments(); 

                if (documents != null && !documents.isEmpty()) {
                    var doc = documents.get(0);
                    // You can iterate through these to pull global attributes
                    // e.g., documents.get(0).getMetadata().getTitle()
                    System.out.println("Processing document metadata for: " + doc.getKind()+" "+doc.getClass());
//                    System.out.println("Document structure: " + Arrays.toString(doc.getClass().getDeclaredMethods()));
                    // If this is the Docling Document object, check for an elements list
                    ExportDocumentResponse content = doc.getContent(); // This is the ExportDocumentResponse object

                    List<Document> springAiDocuments = new ArrayList<>();
                    Map<String, String> headingNodeCache = new HashMap<>(); // Maps heading names to Spring AI Document IDs

                    DoclingDocument doclingDocument = content.getJsonContent();
        
                    if (doclingDocument != null) {
                        System.out.println("Docling Document name: " + doclingDocument.getName());
                        System.out.println("Docling Document furniture: " + doclingDocument.getFurniture().getName()+" "+doclingDocument.getGroups().size()+" "+doclingDocument.getKeyValueItems().size());
                        // Phase A: Ingest groups/structural anchors first
                        for (var item : doclingDocument.getGroups()) {
                            String groupId = documentId + "#group-" + item.getName().hashCode();
                            
                            // Formulate a clean text representation for vector indexing
                            String groupText = "Section Group: " + item.getName(); 
                            
                            Map<String, Object> metadata = new HashMap<>();
                            metadata.put("type", "heading");
                            metadata.put("groupName", item.getName());
                            metadata.put("doclingLabel", item.getMeta() != null ? item.getMeta().toString() : "UNKNOWN");
                            
                            // Extract children reference IDs to build relations
                            List<String> childRefs = new ArrayList<>();
                            for (var childItem : item.getChildren()) {
                                childRefs.add(childItem.getRef());
                            }
                            metadata.put("childReferences", childRefs); // Stored as metadata property array

                            // Create pure Spring AI Document
                            Document groupDoc = new Document(groupId, groupText, metadata);
                            springAiDocuments.add(groupDoc);
                            headingNodeCache.put(item.getName(), groupId);
                        }

                        // Phase B: Process chunks (Paragraphs, Tables, OCR, Equations)
                        var doclingChunks = response.getChunks();
                        for (int i = 0; i < doclingChunks.size(); i++) {
                            var chunk = doclingChunks.get(i);
                            String chunkId = documentId + "#chunk-" + i;
                            
                            Map<String, Object> metadata = new HashMap<>();
                            metadata.put("type", "chunk");
                            metadata.put("pageNumbers", chunk.getPageNumbers());
                            metadata.put("headings", chunk.getHeadings());
                            
                            // Link to previous sequential item using an explicit ID string pointer
                            if (i > 0) {
                                metadata.put("previousChunkId", documentId + "#chunk-" + (i - 1));
                            }

                            // Relate back to parent heading using cached IDs
                            List<String> headingsList = (List<String>) chunk.getHeadings();
                            if (headingsList != null && !headingsList.isEmpty()) {
                                String parentHeaderTitle = headingsList.get(headingsList.size() - 1);
                                if (headingNodeCache.containsKey(parentHeaderTitle)) {
                                    metadata.put("parentSectionId", headingNodeCache.get(parentHeaderTitle));
                                }
                            }

                            // Capture table/equation specific metrics if available in this chunk context
                            // (Assuming you check your docling chunk metadata fields for specialized elements)
                            if (chunk.getMetadata().containsKey("table_mask")) {
                                metadata.put("type", "table");
                                metadata.put("raw_table_data", chunk.getMetadata().get("table_cells"));
                            }

                            Document chunkDoc = new Document(chunkId, chunk.getText(), metadata);
                            springAiDocuments.add(chunkDoc);
                        }
                    }
                    else System.out.println("Docling Document is null from content.getJsonContent() " );
                }
                var doclingChunks = response.getChunks();
                if (doclingChunks == null || doclingChunks.isEmpty()) return response;

        List<Document> springAiDocs = new ArrayList<>();

        // Caches section header ID strings instead of entity objects
        Map<String, String> headingNodeCache = new HashMap<>();

        for (int i = 0; i < doclingChunks.size(); i++) {
            var chunk = doclingChunks.get(i);
            String textContent = chunk.getText();
            if (textContent == null) textContent = chunk.toString();
            
            String compositeId = documentId + "#chunk-" + i;

            // 2. Build a flat metadata map for Spring AI
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("page_numbers", (List<Integer>) chunk.getPageNumbers());
            
            List<String> headingsList = (List<String>) chunk.getHeadings();
            if (headingsList != null) {
                metadata.put("headings", headingsList);
            }

            metadata.put("type", "text");
            boolean isHeaderNode = false;
            String parentHeaderTitle = null;

            if (headingsList != null && !headingsList.isEmpty()) {
                parentHeaderTitle = headingsList.get(headingsList.size() - 1);
                String cleanText = textContent.trim().toLowerCase();
                String cleanHeader = parentHeaderTitle.trim().toLowerCase();
                
                if (i == 0 || cleanText.equals(cleanHeader)) {
                    metadata.put("type", "heading");
                    isHeaderNode = true;
                    headingNodeCache.put(parentHeaderTitle, compositeId);
                }
            }

            // RELATIONSHIP 1: Chronological Link (Store the next ID pointer as metadata)
            if (i > 0) {
                // Update the previous document in our list to point to this one as the next chunk
                Document previousDoc = springAiDocs.get(springAiDocs.size() - 1);
                previousDoc.getMetadata().put("nextChunkId", compositeId);
            }

            // RELATIONSHIP 2: Hierarchical Tree Link (Store parent ID pointers as metadata)
            if (isHeaderNode) {
                if (headingsList.size() > 1) {
                    String superiorHeader = headingsList.get(headingsList.size() - 2);
                    if (headingNodeCache.containsKey(superiorHeader)) {
                        metadata.put("parentSectionId", headingNodeCache.get(superiorHeader));
                    }
                }
            } else {
                if (parentHeaderTitle != null) {
                    if (!headingNodeCache.containsKey(parentHeaderTitle)) {
                        // Create a virtual header document if Docling skipped it
                        String virtualId = documentId + "#header-" + parentHeaderTitle.hashCode();
                        Map<String, Object> virtualMeta = new HashMap<>();
                        virtualMeta.put("type", "heading");
                        
                        Document virtualHeader = new Document(virtualId, parentHeaderTitle, virtualMeta);
                        headingNodeCache.put(parentHeaderTitle, virtualId);
                        springAiDocs.add(virtualHeader);
                    }
                    
                    metadata.put("parentSectionId", headingNodeCache.get(parentHeaderTitle));
                }
            }

            // 3. Instantiate the pure Spring AI Document
            Document currentDoc = new Document(compositeId, textContent, metadata);
            springAiDocs.add(currentDoc);
        }
            System.out.println("Adding " + springAiDocs.size() + " chunks to the vector store.");
            vectorStore.add(springAiDocs);
            System.out.println("Chunks added to vector store successfully.");
            try {
                // Gives your laptop hard drive a brief window to commit the 60 embedding vectors safely
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            String linkChronological = """
                MATCH (a:CustomDocument), (b:CustomDocument)
                WHERE a.`metadata.nextChunkId` = b.id
                MERGE (a)-[:NEXT]->(b)
                """;

            // 2. Updated Hierarchical Tree Stitching Query
            String linkHierarchical = """
                MATCH (child:CustomDocument), (parent:CustomDocument)
                WHERE child.`metadata.parentSectionId` IS NOT NULL
                AND child.`metadata.parentSectionId` = parent.id
                MERGE (parent)-[:HAS_CHILD]->(child)
                """;

            neo4jClient.query(linkChronological).run();
            neo4jClient.query(linkHierarchical).run();
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

