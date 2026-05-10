package org.woodchuck.services;

import ai.docling.serve.api.DoclingServeApi;
import ai.docling.serve.api.chunk.request.HybridChunkDocumentRequest;
import ai.docling.serve.api.chunk.request.options.ChunkerOptions;
import ai.docling.serve.api.chunk.request.options.HybridChunkerOptions;
import ai.docling.serve.api.chunk.response.ChunkDocumentResponse;
import ai.docling.serve.api.convert.request.ConvertDocumentRequest;
import ai.docling.serve.api.convert.request.source.HttpSource;
import ai.docling.serve.api.convert.response.ConvertDocumentResponse;
import ai.docling.serve.api.convert.response.InBodyConvertDocumentResponse;

//import ai.docling.serve.client.operations.ChunkOperations;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.woodchuck.services.OllamaEmbeddingService;

@Service
public class DoclingAsyncService {

    @Value("${arconia.docling.polling.delay-seconds:2}")
    private int pollDelaySeconds;

    @Value("${arconia.docling.polling.max-attempts:60}")
    private int maxAttempts;

    private final DoclingServeApi doclingServeApi;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final OllamaEmbeddingService embeddingService;

    public DoclingAsyncService(DoclingServeApi doclingServeApi, OllamaEmbeddingService embeddingService) {
        this.doclingServeApi = doclingServeApi;
        this.embeddingService = embeddingService;
    }

    public CompletableFuture<ConvertDocumentResponse> processDocumentAsync(ConvertDocumentRequest request) {
        CompletableFuture<ConvertDocumentResponse> resultFuture = new CompletableFuture<>();
        pollConvertSource(request, resultFuture, 0);
        resultFuture.whenComplete((response, throwable) -> {
            if (throwable != null) {
                System.err.println("Document conversion failed: " + throwable.getMessage());
            } else {
                System.out.println("Document conversion succeeded: ");
   
                if (response instanceof InBodyConvertDocumentResponse inBodyResult) {
                    String markdown = inBodyResult.getDocument().getMarkdownContent();
                    Document doc = new Document(markdown);
                    // Chunking (Split into smaller pieces)
                    TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(700).build();
                    List<Document> chunks = splitter.apply(List.of(doc));
                    // var tokenizer = HuggingFaceTokenizer.fromPretrained("Xenova/text-embedding-ada-002")
                    // HybridChunkDocumentRequest chunker = new HybridChunkDocumentRequest();
                    // HybridChunkerOptions chunkingOptions = chunker.getChunkingOptions();//.setChunkSize(700);
                    // chunker.setChunkOverlap(280);
                    // chunkesetMaxTokens(8000);
                    // chunker.setIncr.ludeMetadata(true);
                    // //chunker.setTokenizer(OpenAiTokenizer.builder().build());
                    // chunker.setTextSplitter(TokenTextSplitter.builder().withChunkSize(700).build());
                    // List<Document> chunks = chunker.chunk(doc);
                    chunks.forEach(chunk -> {
                    //    System.out.println("Chunk: " + chunk.getText());
                        System.out.println("Metadata: " + chunk.getMetadata());
                        float[] vecs = embeddingService.getEmbeddings(chunk.getText());
                        System.out.println("Embedding length: " + vecs.length);
                    });
                }
            }
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
