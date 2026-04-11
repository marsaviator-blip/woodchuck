package org.woodchuck.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class CitationService {

    private final WebClient webClient;
    private final String API_KEY = "YOUR_SERPAPI_KEY";

    public CitationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://serapi.com").build();
    }
    public Mono<String> getCitation(String citationId) {
        return this.webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("search")
                .queryParam("engine","g oogle_scholar_cite")
                .queryParam("q", citationId)
                .queryParam("api_key", API_KEY)
                .build())
            .retrieve()
            .bodyToMono(String.class); // or a specific POJO
    }
}
