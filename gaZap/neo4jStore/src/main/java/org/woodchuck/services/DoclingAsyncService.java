package org.woodchuck.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

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

    public DoclingAsyncService(DoclingServeApi doclingServeApi, VectorStore vectorStore) {
        this.doclingServeApi = doclingServeApi;
        this.vectorStore = vectorStore;
    }

    public CompletableFuture<ChunkDocumentResponse> processDocumentAsync(HybridChunkDocumentRequest request) {
        CompletionStage<ChunkDocumentResponse> stage = doclingServeApi.chunkSourceWithHybridChunkerAsync(request);
        CompletableFuture<ChunkDocumentResponse> resultFuture =stage.toCompletableFuture().thenApply(response -> {
                System.out.println("Document conversion succeeded: ");
   
                    List<Document> chunks = response.getChunks().stream()
                            .map(doclingChunk -> {
                                // 1. Extract the text content from the Docling chunk
                                String text = doclingChunk.getText();
                                //System.out.println("Chunk text: " + text); // Debug: Print the chunk text
                                // 2. Map Docling metadata (headings, pages, etc.) to a Map
                                Map<String, Object> metadata = new HashMap<>();
                                // Use the flattened getters directly on the chunk
                                if (doclingChunk.getHeadings() != null) {
                                    metadata.put("headings", doclingChunk.getHeadings());
                                }
                                
                                // Page info or unique identifier
                                if (doclingChunk.getPageNumbers() != null) {
                                    metadata.put("page_numbers", doclingChunk.getPageNumbers());
                                }

                                return (text != null) ? new org.springframework.ai.document.Document(text, metadata) : null;
                            })
                            .filter(Objects::nonNull)
                            .toList();
                    // chunks.forEach(chunk -> {
                    // //    System.out.println("Chunk: " + chunk.getText());
                    //     System.out.println("Metadata: " + chunk.getMetadata());
                    //     //float[] vecs = embeddingService.getEmbeddings(chunk.getText());
                    //     //System.out.println("Embedding length: " + vecs.length);
                    //     messageSender.sendMessage("Chunk: " + chunk.getText() + " | Metadata: " + chunk.getMetadata() );
                    // });
                    vectorStore.add(chunks);
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
