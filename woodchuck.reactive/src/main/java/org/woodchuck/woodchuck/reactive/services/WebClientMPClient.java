package org.woodchuck.woodchuck.reactive.services;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class WebClientMPClient {
    
    private final WebClient webClient = WebClient.create("http://localhost:8080");

    public Flux<StockPrice> retrieveStockPrices() {
        return webClient.get()
            .uri("/stocks/{symbol}", "MSFT")
            .retrieve()
            .bodyToFlux(StockPrice.class); // Reactive stream of data
    }
}
