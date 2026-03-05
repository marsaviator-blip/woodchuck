package org.woodchuck.woodchuck.mvc.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class MPservice {
        private final RestClient restClient;

    public MPService() {
        // Initialize the RestClient with a base URL
        restClient = RestClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    public List<Post> findAll() {
        return restClient.get()
                .uri("/posts") // Specify the endpoint URI
                .retrieve()    // Execute the request and retrieve the response
                .body(new ParameterizedTypeReference<List<Post>>() {}); // Map the response body to a List of Post objects
    }

}
