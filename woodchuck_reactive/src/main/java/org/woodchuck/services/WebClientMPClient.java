package org.woodchuck.services;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class WebClientMPClient {
    
    private final WebClient webClient = WebClient.create("http://localhost:8080");

    public Flux<String> retrieveStockPrices() {
        return webClient.get()
            .uri("/stocks/{symbol}", "MSFT")
            .retrieve()
            .bodyToFlux(String.class); // Reactive stream of data
    }
}
