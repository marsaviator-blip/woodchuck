package org.woodchuck.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.woodchuck.services.WebClientMPClient;

@Component
public class ClientRunner implements CommandLineRunner {
    

    private final WebClientMPClient mpService;

    // Constructor injection
    public ClientRunner(WebClientMPClient mpService) {
        this.mpService = mpService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Fetching all posts using RestClient:");
        mpService.retrieveStockPrices().subscribe(System.out::println);
    }

}
