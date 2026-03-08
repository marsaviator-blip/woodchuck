package org.woodchuck.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class MPService {

    private final RestClient restClient;

    private final String BASE_URL = "https://api.materialsproject.org";
    private final String API_KEY =  "your_api_key_here"; // Replace with your actual API key

    public MPService(RestClient.Builder builder) {
        // RestClient.Builder is auto-configured by Spring boot
        // with useful settings like metrics abd message converters.

        // Initialize the RestClient with a base URL
        this.restClient = builder
                .baseUrl(BASE_URL)
                .defaultHeader("X-API-KEY", API_KEY) // Add API key to the header for authentication
                .build();
    }

    // not to be used, just for demonstration
    public List<String> findAll() {
        return restClient.get()
                .uri("/posts") // Specify the endpoint URI
                .retrieve() // Execute the request and retrieve the response
                .body(new ParameterizedTypeReference<List<String>>() {
                }); // Map the response body to a List of Post objects
    }

    //
    public List<String> getChemicalElement(String elementId) {
        return  restClient.get()
                .uri("/materials/summary?formula={elementId}", elementId) 
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    // Add more methods to interact with other endpoints of the Materials Project
    // API as needed

}
