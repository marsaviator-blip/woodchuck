package org.woodchuck.zChecker.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
            // Base URL of the target microservice
            .baseUrl("http://localhost:8087") // pgStore
            .build();
    }
}
