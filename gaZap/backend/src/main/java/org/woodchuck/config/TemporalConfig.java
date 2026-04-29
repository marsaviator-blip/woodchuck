package org.woodchuck.config;

import org.springframework.context.annotation.Configuration;
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
}
