package org.woodchuck.configs;

import org.woodchuck.dtos.SearchRequest;
import org.woodchuck.dtos.SearchResponseList;  
import org.woodchuck.dtos.SearchResult;
import org.woodchuck.services.SearchService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.function.Function;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class WebDiscoveryTools {

    final SearchService searchService;

    WebDiscoveryTools(SearchService searchService) {
        this.searchService = searchService;
    }

    @Bean
    @Description("Queries external search indexes to find relevant live web page URLs for a specific subject matter") Function<SearchRequest, SearchResponseList> discoverSubjectWebsites() {
        return request -> {
            System.out.println("Discovering subject websites for query: " + request.subjectQuery() + " with limit: " + request.maxLinksToReturn());
            // Replace with your preferred external API client (e.g., Brave Search, Google Custom Search, Serper)
            List<SearchResult> scrapedMeta = searchService.callExternalSearchEngineApi(request.subjectQuery(), request.maxLinksToReturn());
            return new SearchResponseList(scrapedMeta);
        };
    }

//     private List<SearchResult> callExternalSearchEngineApi(String query, int limit) {
//         // Mock execution pattern
//         return List.of(
//             new SearchResult(
//                 "Introduction to Subject", 
//                 "https://example.com", 
//                 "Detailed layout overview..."),
//             new SearchResult(
//                 "Advanced Subject Methodology", 
//                 "https://www.google.com/", 
//                 "Diving deep into unstructured systems...")
//         );
//     }
//     private List<SearchResult> callExternalSearchEngineApi(String query, int limit) {
//     try {
//         // 1. Build the API request URL
//         String url = "https://example.com" + 
//                      URLEncoder.encode(query, StandardCharsets.UTF_8) + 
//                      "&limit=" + limit;

//         HttpClient client = HttpClient.newHttpClient();
//         HttpRequest request = HttpRequest.newBuilder()
//                 .uri(URI.create(url))
//                 .header("Authorization", "Bearer YOUR_API_KEY") // 2. Add API key
//                 .GET()
//                 .build();

//         // 3. Send the request
//         HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

//         // 4. Parse the JSON response and return real data
//         return parseJsonToSearchResults(response.body());

//     } catch (Exception e) {
//         // Handle network or parsing errors gracefully
//         e.printStackTrace();
//         return List.of(); 
//     }
// }

}
