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

   //   @Bean
   //  public DoclingServeApi doclingServeApi(@Value("${arconia.dev.services.docling.serve.url}") String baseUrl) {
   //      return DoclingServeApi.builder().readTimeout(Duration.ofSeconds(600))
   //          .baseUrl(baseUrl)
   //          .build();
   //  }

}
