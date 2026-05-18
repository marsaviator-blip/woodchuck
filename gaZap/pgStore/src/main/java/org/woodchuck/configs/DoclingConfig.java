package org.woodchuck.configs;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.docling.serve.api.DoclingServeApi;

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
}
