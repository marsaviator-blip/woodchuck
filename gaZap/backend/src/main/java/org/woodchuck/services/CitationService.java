package org.woodchuck.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.woodchuck.components.ApiKeyProperties;

@Service
public class CitationService {

    private final RestClient restClient;
    private final String BASE_URL = "https://serapi.com";
    private String API_KEY;

    public CitationService(RestClient.Builder restClientBuilder, ApiKeyProperties apiKeyProperties) {
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(100))
            .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
            requestFactory.setReadTimeout(Duration.ofSeconds(60));
            this.API_KEY = apiKeyProperties.getSerpApiKey();
        this.restClient = restClientBuilder.baseUrl(BASE_URL)
//            .requestFactory(requestFactory)
            .defaultHeader("X-API-KEY", API_KEY) // Add API key to the header for authentication
            .build();
    }
    public String getCitation(String astmId) {
        URI targetUrl = UriComponentsBuilder.fromUriString(BASE_URL)
            .path("/search")
            .queryParam("engine", "google_scholar_cite")
            .queryParam("q", "ASTM+" + astmId)
            .build()
            .toUri();
        System.out.println("Final URI: " + targetUrl.toString());

        return this.restClient.get()
            .uri(targetUrl)
            .retrieve()
            .body(String.class);
    }
}
