package org.woodchuck.services;

import ai.docling.serve.api.DoclingServeApi;
import ai.docling.serve.api.convert.request.ConvertDocumentRequest;
import ai.docling.serve.api.convert.response.ConvertDocumentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.CompletableFuture;
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

    public DoclingAsyncService(DoclingServeApi doclingServeApi) {
        this.doclingServeApi = doclingServeApi;
    }

    public CompletableFuture<ConvertDocumentResponse> processDocumentAsync(ConvertDocumentRequest request) {
        CompletableFuture<ConvertDocumentResponse> resultFuture = new CompletableFuture<>();
        pollConvertSource(request, resultFuture, 0);
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
