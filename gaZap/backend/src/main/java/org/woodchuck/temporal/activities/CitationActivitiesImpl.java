package org.woodchuck.temporal.activities;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.woodchuck.components.ApiKeyProperties;

@ActivityImpl(taskQueues = "CitationQueue")
public class CitationActivitiesImpl implements CitationActivities {

    private final RestClient restClient;
    private static final String BASE_URL = "https://serpapi.com";
    private static  String API_KEY = System.getenv("SERPAPI_KEY");

    public CitationActivitiesImpl(RestClient.Builder builder, ApiKeyProperties apiKeyProperties) {
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(100))
            .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
            requestFactory.setReadTimeout(Duration.ofSeconds(60));
            this.API_KEY = apiKeyProperties.getSerpApiKey();
        System.out.println("SERP_API_KEY: " + this.API_KEY);    
        this.restClient = builder.baseUrl(BASE_URL)
//            .requestFactory(requestFactory)
            .defaultHeader("X-API-KEY", API_KEY) // Add API key to the header for authentication
            .build();
    }
    @Override
    public String getCitations(String astmId) {
        URI targetUrl = UriComponentsBuilder.fromUriString(BASE_URL)
            .path("/search")
            .queryParam("engine", "google_scholar_cite")
            .queryParam("q", "ASTM+" + astmId)
            .queryParam("api_key", this.API_KEY)
            .queryParam("location", "USA")
            .build()
            .toUri();
        System.out.println("Final URI: " + targetUrl.toString());

        return this.restClient.get()
            .uri(targetUrl)
            .retrieve()
            .body(String.class);
    }
}
