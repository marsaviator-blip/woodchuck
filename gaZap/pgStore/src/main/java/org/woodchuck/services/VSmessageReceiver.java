package org.woodchuck.services;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import ai.docling.serve.api.convert.request.source.HttpSource;
import ai.docling.serve.api.chunk.request.HybridChunkDocumentRequest;
import ai.docling.serve.api.chunk.response.ChunkDocumentResponse;
import ai.docling.serve.api.convert.request.ConvertDocumentRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class VSmessageReceiver {
    
    private static final Logger logger = LoggerFactory.getLogger(VSmessageReceiver.class);
    private static final String TOPIC_NAME = "woodchuck";
    private final DoclingAsyncService doclingAsyncService;

    public VSmessageReceiver(DoclingAsyncService doclingAsyncService){
        this.doclingAsyncService = doclingAsyncService;
    }

    @KafkaListener(topics = TOPIC_NAME)
    public void receiveMessage(String url) {
        logger.info("Received message: {}", url);
        // Process the message as needed
        CompletableFuture<ChunkDocumentResponse> future = doclingAsyncService.processDocumentAsync(
                HybridChunkDocumentRequest.builder()
                        .source(HttpSource.builder().url(URI.create(url)).build())
                        .build());
        //future.state().toString();
    }
}
