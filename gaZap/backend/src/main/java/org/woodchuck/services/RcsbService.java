package org.woodchuck.services;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.woodchuck.components.ApiKeyProperties;
import org.woodchuck.components.CustomRequestInterceptor;
import org.woodchuck.dtos.SearchQueryParams;

import com.fasterxml.jackson.databind.node.ObjectNode;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

@Service
public class RcsbService {
    private final RestClient restClient;

    private final String BASE_URL = "https://search.rcsb.org";

	public RcsbService(RestClient.Builder builder, ApiKeyProperties apiKeyProperties, CustomRequestInterceptor customRequestInterceptor) {
        String API_KEY = apiKeyProperties.getMpApiKey();
        System.out.println("MP_API_KEY: " + API_KEY);

        // Initialize the RestClient with a base URL
        this.restClient = builder
                .requestInterceptor(customRequestInterceptor)
                .baseUrl(BASE_URL)
                .build();
	}

    public List<String> search(SearchQueryParams params) {
        URI targetUrl = UriComponentsBuilder.fromUriString(BASE_URL)
            .path("/rcsbsearch/v2/query")
            .query(params.getQuery())
            .build(false)
            .toUri();
        String response = restClient.get()
                .uri(targetUrl) // Specify the endpoint URI
                .retrieve() // Execute the request and retrieve the response
                .body(String.class); // Map the response body to a List of Post objects
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode identifierNode = rootNode.path("result_set"); // Get the named array
            List<String> identifiers = new ArrayList<>(); // get real stuff
            
            for (JsonNode node : identifierNode) {
//                System.out.println(node);
                String id = node.get("identifier").asString();
                identifiers.add(id);
            }   
        
        return identifiers;
    }
}
