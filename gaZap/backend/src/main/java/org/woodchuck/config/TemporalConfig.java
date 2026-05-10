package org.woodchuck.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.docling.serve.api.DoclingServeApi;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowClientOptions.Builder;
import io.temporal.spring.boot.TemporalOptionsCustomizer;

@Configuration
public class TemporalConfig  implements TemporalOptionsCustomizer<WorkflowClientOptions.Builder>{
    
 @Override
 public Builder customize(Builder optionsBuilder) {
    // TODO Auto-generated method stub
    optionsBuilder.setIdentity("woodchuck-client");
    optionsBuilder.setNamespace("default");

    return optionsBuilder;
 }

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
