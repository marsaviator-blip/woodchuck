package org.woodchuck.services;

import ai.docling.serve.api.DoclingServeApi;
import ai.docling.serve.api.convert.request.ConvertDocumentRequest;
import ai.docling.serve.api.convert.response.ConvertDocumentResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class DoclingAsyncService {

    private final DoclingServeApi doclingServeApi;
    private final Executor executor = Executors.newFixedThreadPool(4);

    public DoclingAsyncService(DoclingServeApi doclingServeApi) {
        this.doclingServeApi = doclingServeApi;
    }

    public CompletableFuture<ConvertDocumentResponse> processDocumentAsync(ConvertDocumentRequest request) {
        return CompletableFuture.supplyAsync(() -> doclingServeApi.convertSource(request), executor);
    }
}
