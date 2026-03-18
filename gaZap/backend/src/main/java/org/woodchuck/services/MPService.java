package org.woodchuck.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.woodchuck.components.ApiKeyProperties;
import org.woodchuck.components.CustomRequestInterceptor;
import org.woodchuck.dtos.MaterialStructureParams;

import java.io.File;
import java.util.List;

@Service
public class MPService {

    private final RestClient restClient;

    private final String BASE_URL = "https://api.materialsproject.org";

    public MPService(RestClient.Builder builder, ApiKeyProperties apiKeyProperties, CustomRequestInterceptor customRequestInterceptor) {

        // String MP_API_KEY = env.getProperty("MP"); // need to haveset environment
        // variable for your OS
        // RestClient.Builder is auto-configured by Spring boot
        // with useful settings like metrics and message converters.

        String API_KEY = apiKeyProperties.getMpApiKey();
        System.out.println("MP_API_KEY: " + API_KEY);

        // Initialize the RestClient with a base URL
        this.restClient = builder
                .requestInterceptor(customRequestInterceptor)
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
    public String getChemicalElement(String elementId) {
        return restClient.get()
                .uri("/materials/summary/?formula={elementId}", elementId)
                .retrieve()
                .body(String.class); //new ParameterizedTypeReference<List<String>>() {
                
    }
    // Add more methods to interact with other endpoints of the Materials Project
    // API as needed

    public String getMaterialDetails(MaterialStructureParams params) {
        return restClient.get()
                .uri("/materials/core/?material_ids={materialId}"+
                "&_fields={fields}&deprecated={deprecated}&_per_page={perPage}"+
                "&_skip={skip}&_limit={limit}&license={license}", 
                params.getMaterial_id(), params.get_fields(), params.isDeprecated(), 
                params.get_per_page(), params.get_skip(), params.get_limit(), params.getLicense())
                .retrieve()
                .body(String.class); //new ParameterizedTypeReference<List<String>>() {});
    }

    public String getCIFfile(String materialId) {
        return restClient.get()
                .uri("/materials/cif?type=symmeterized&material_ids={materialId}", materialId)
                .retrieve()
                .body(String.class); //new ParameterizedTypeReference<List<String>>() {});      
    }

    // .uri("/materials/summary/get_data_by_id/{materialId}", materialId
}
