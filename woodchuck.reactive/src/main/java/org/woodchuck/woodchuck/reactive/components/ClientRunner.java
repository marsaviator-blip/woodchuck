package org.woodchuck.woodchuck.reactive.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ClientRunner {
    

    private final MPService mpService;

    // Constructor injection
    public ClientRunner(MPService mpService) {
        this.mpService = mpService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Fetching all posts using RestClient:");
        mpService.findAll().forEach(System.out::println);
    }

}
