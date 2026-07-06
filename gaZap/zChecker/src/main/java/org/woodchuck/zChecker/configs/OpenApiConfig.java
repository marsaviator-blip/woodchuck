package org.woodchuck.zChecker.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI scholarlyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Scholarly Document AI Pipeline API")
                        .description("REST endpoints for ETL ingestion, metadata-filtered hybrid vector search, and strict RAG query operations using Spring AI.")
                        .version("1.0.0"));
    }
}
