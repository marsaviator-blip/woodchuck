package org.woodchuck.configs;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;

import ai.docling.serve.api.DoclingServeApi;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowClientOptions.Builder;
import io.temporal.spring.boot.TemporalOptionsCustomizer;

import javax.sql.DataSource;

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

    /**
     * POLITE CLIENT: This bean only compiles and hooks into the runtime
     * when --spring.profiles.active=embeddings is passed.
     */
    @Bean
    public RestClient crossrefRestClient(
            @Value("${crossref.api.base-url:https://api-crossref.org}") String baseUrl,
            @Value("${crossref.api.contact-email}") String email) {
        
        return RestClient.builder()
            .baseUrl(baseUrl)
            // Automates Crossref compliance invisibly behind the scenes
            .defaultHeader(HttpHeaders.USER_AGENT, "WoodchuckIngestionEngine/1.0 (mailto:" + email + ")")
            .build();
    }

    // 1. Build Crossref Data Source (Designated as Primary)
    @Primary
    @Bean(name = "crossrefDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.crossref-db")
    public DataSource crossrefDataSource() {
        return DataSourceBuilder.create().build();
    }

    // 2. Build Keycloak Data Source
    @Bean(name = "keycloakDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.keycloak")
    public DataSource keycloakDataSource() {
        return DataSourceBuilder.create().build();
    }

    // 3. Create Corresponding JdbcTemplates for raw SQL execution
    @Primary
    @Bean(name = "crossrefJdbcTemplate")
    public JdbcTemplate crossrefJdbcTemplate(DataSource crossrefDataSource) {
        return new JdbcTemplate(crossrefDataSource);
    }

    @Bean(name = "keycloakJdbcTemplate")
    public JdbcTemplate keycloakJdbcTemplate(DataSource keycloakDataSource) {
        return new JdbcTemplate(keycloakDataSource);
    }
}
