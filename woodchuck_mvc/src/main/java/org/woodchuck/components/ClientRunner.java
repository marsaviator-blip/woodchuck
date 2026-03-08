package org.woodchuck.components;

import java.util.List;

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

    public void fetchChemicalElement(String element) {
        System.out.println("Fetching chemical element data for: " + element);

        List<String> data = mpService.getChemicalElement(element); // Fetch and print chemical element data
        System.out.println("Tried fetching chemical element data for: " + element);
        if (data != null && !data.isEmpty()) {
            System.out.println("Data fetched successfully:");
            data.forEach(System.out::println);
        } else {
            System.out.println("No data found for element: " + element);
        }
        System.out.println("Finished fetching chemical element data for: " + element);
    }

    // this method can be replaced with REST API calls to fetch data from the
    // Materials Project API using the MPService methods
    @Override
    public void run(String... args) {
        System.out.println("Fetching  using RestClient:");
        String element = "CaHPO4"; // Example component ID, replace with actual ID as needed
        fetchChemicalElement(element); // Fetch and print chemical element data

        // do interesting things with the service here, like fetching data or performing
        // operations

        // give nice names to the methods in MPService and use them here, for example:

    }

    // public static void main(String[] args) {
    // This main method is not needed for Spring Boot applications, as the
    // application
    // will be started by Spring Boot's auto-configuration. However, you can use it
    // for testing purposes if needed.
    // }

}
