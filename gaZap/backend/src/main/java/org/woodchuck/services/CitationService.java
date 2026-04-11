package org.woodchuck.services;

import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.client.RestClient;
import org.woodchuck.components.ApiKeyProperties;

@Service
public class CitationService {

    private final RestClient restClient;
    private final String BASE_URL = "https://serapi.com";
    private String API_KEY;

    public CitationService(RestClient.Builder restClientBuilder, ApiKeyProperties apiKeyProperties) {
        this.API_KEY = apiKeyProperties.getSerpApiKey();
        this.restClient = restClientBuilder.baseUrl(BASE_URL)
            .defaultHeader("X-API-KEY", API_KEY) // Add API key to the header for authentication
            .build();
    }
    public String getCitation(String citationId) {
        return this.restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("search")
                .queryParam("engine","google_scholar_cite")
                .queryParam("q", citationId)
//                .queryParam("api_key", this.API_KEY)
                .build())
            .retrieve()
            .body(String.class);
            //.bodyToMono(String.class); // or a specific POJO
    }
}
