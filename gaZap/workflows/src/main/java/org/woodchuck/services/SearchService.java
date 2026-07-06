package org.woodchuck.services;

import org.woodchuck.dtos.SearchResult;
import org.woodchuck.dtos.SearchResponseList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.search.cx}")
    private String searchEngineId;

    private final RestClient restClient = RestClient.create();

    public List<SearchResult> callExternalSearchEngineApi(String query, int limit) {
        try {
            System.out.println(apiKey+" "+searchEngineId+" "+query+" "+limit);
            // Call the official Google Custom Search JSON API
            SearchResponseList response = restClient.get()
                .uri("https://googleapis.com/customsearch/v1?{key}&cx={cx}&q={q}&num={num}", 
                     apiKey, searchEngineId, query, limit)
                .retrieve()
                .body(SearchResponseList.class);
System.out.println("Google Search API response: " + response);
            if (response == null || response.linksFound() == null) {
                return List.of();
            }

            // Convert Google's format into your local SearchResult objects
            return response.linksFound().stream()
                .map(item -> new SearchResult(item.title(), item.url(), item.briefSnippet()))
                .collect(Collectors.toList());

        } catch (Exception e) {
            // Log the error and fail gracefully by returning an empty list
            System.err.println("Google Search API call failed: " + e.getMessage());
            return List.of();
        }
    }
}

