package org.woodchuck.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.woodchuck.services.MPService;

@Component
public class ClientRunner implements CommandLineRunner {
    

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
