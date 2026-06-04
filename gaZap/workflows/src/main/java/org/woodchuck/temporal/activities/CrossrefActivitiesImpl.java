package org.woodchuck.temporal.activities;

import java.net.URI;

import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.woodchuck.dtos.CitedReferencesResult;
import org.woodchuck.dtos.CrossrefSearchResponse;
import org.woodchuck.dtos.CrossrefXmlResponse;
import org.springframework.stereotype.Component;
import io.temporal.spring.boot.ActivityImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.xml.XmlMapper;

@Component
@ActivityImpl(taskQueues = "CrossrefQueue")
public class CrossrefActivitiesImpl implements CrossrefActivities {

    private static final String BASE_URL = "https://api.crossref.org";

    private final RestClient restClient;

    public CrossrefActivitiesImpl(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public CrossrefXmlResponse getWorks(String doi) {
        System.out.println("CrossrefActivitiesImpl.getWorks called with DOI: " + doi);
        URI targetUrl = UriComponentsBuilder.fromUriString(BASE_URL)
            .path("/works/{doi}/transform")
            .buildAndExpand(doi)
            .toUri();
System.out.println("CrossrefActivitiesImpl.getWorks: targetUrl=" + targetUrl);  
        String xmlRawPayload = restClient.get()
            .uri(targetUrl)
            .header("Accept","application/vnd.crossref.unixsd+xml")
            .retrieve()
            .body(String.class);
            try {
                System.out.println("Raw XML payload received: " + xmlRawPayload); // Debug: Print raw XML response
                XmlMapper xmlMapper = new XmlMapper();
                return xmlMapper.readValue(xmlRawPayload, CrossrefXmlResponse.class);
            } catch (Exception e) {
                throw new RuntimeException("Jackson processing error on Crossref content", e);
            }
    }

    @Override
    public CitedReferencesResult getWorksBy(String citeKey, String title, String author) {
        System.out.println("CrossrefActivitiesImpl.getWorks called with title: " + title + ", author: " + author);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(BASE_URL)
            .path("/works");

        if (title != null && !title.isBlank()) {
            uriBuilder.queryParam("query.title", title);
        }
        if (author != null && !author.isBlank()) {
            uriBuilder.queryParam("query.author", author);
        }

        URI targetUrl = uriBuilder.build().toUri();
System.out.println("CrossrefActivitiesImpl.getWorks: targetUrl=" + targetUrl);  
        String jsonPayload = restClient.get()
            .uri(targetUrl)
            // .header("Accept", "application/vnd.crossref.unixsd+xml")
            .retrieve()
            .body(String.class);
            try{
                ObjectMapper jsonMapper = new ObjectMapper();
                return new CitedReferencesResult(citeKey, jsonMapper.readValue(jsonPayload, CrossrefSearchResponse.class));
            } catch (Exception e) {
                throw new RuntimeException("Error processing Crossref search response", e);
            }
    }

}
