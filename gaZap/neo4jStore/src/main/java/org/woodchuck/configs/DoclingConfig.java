package org.woodchuck.configs;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.docling.serve.api.DoclingServeApi;
import ai.docling.serve.api.chunk.request.options.HybridChunkerOptions;
import ai.docling.serve.api.convert.request.options.OutputFormat;
import ai.docling.serve.api.convert.request.options.ConvertDocumentOptions;

@Configuration
public class DoclingConfig {
    
    @Bean
    public DoclingServeApi doclingServeApi(@Value("${arconia.dev.services.docling.serve.url}") String baseUrl,
                                            @Value("${arconia.dev.services.docling.serve.timeout-seconds}") Duration timeoutSeconds,
                                            @Value("${arconia.dev.services.docling.serve.async-timeout}") Duration asyncTimeout,
                                            @Value("${arconia.dev.services.docling.serve.async-poll-interval}") Duration asyncPollInterval) {
        return DoclingServeApi.builder().readTimeout(timeoutSeconds)
            .asyncPollInterval(asyncPollInterval) // Match the polling delay configured in application.yaml
            .asyncTimeout(asyncTimeout) // Set a timeout for async operations
            .baseUrl(baseUrl)
            .build();
    }

    @Bean
    public ConvertDocumentOptions detailedDoclingOptions() {
        // Enforces structural layout data retention over simple raw markdown
        return ConvertDocumentOptions.builder()
            .toFormat(OutputFormat.MARKDOWN) // Lossless JSON yields structural hierarchies
            .doOcr(true)                     // Essential for parsing formula text nodes
            .doTableStructure(true)          // Preserves tabular algorithmic alignment
            .build();
    }

    @Bean
    public HybridChunkerOptions defaultHybridOptions() {
        return HybridChunkerOptions.builder()
            .mergePeers(false) // Enforces semantic sentence merging boundaries
            // .maxTokens(600) // Optional: Adjust max chunk boundaries here if needed
            .build();
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder.requestInterceptor((request, body, execution) -> {
            // This runs BEFORE serialization or network handoff can fail
            System.out.println("=== CRITICAL OUTBOUND TRACE ===");
            System.out.println("HTTP METHOD : " + request.getMethod());
            System.out.println("ABSOLUTE URI: " + request.getURI());
            System.out.println("===============================");
            
            return execution.execute(request, body);
        });
    }
    
}
