package org.woodchuck.services;

import ai.docling.serve.api.convert.request.source.HttpSource;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class VSmessageReceiver {
    
    private static final Logger logger = LoggerFactory.getLogger(VSmessageReceiver.class);
    private static final String TOPIC_NAME = "woodchuck";

    @KafkaListener(topics = TOPIC_NAME)
    public void receiveMessage(String url) {
        logger.info("Received message: {}", url);
        // Process the message as needed
        CompletableFuture<ChunkDocumentResponse> future = doclingAsyncService.processDocumentAsync(
                HybridChunkDocumentRequest.builder()
                        .source(HttpSource.builder().url(URI.create(url)).build())
                        .build());
        
    }
}
