package org.woodchuck.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class MPService {
    
    private final RestClient restClient;

    public MPService() {
        
        // Initialize the RestClient with a base URL
        restClient = RestClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    public List<String> findAll() {
        return restClient.get()
                .uri("/posts") // Specify the endpoint URI
                .retrieve()    // Execute the request and retrieve the response
                .body(new ParameterizedTypeReference<List<String>>() {}); // Map the response body to a List of Post objects
    }

}
