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

    private final RestClient crossrefRestClient;
    private final java.util.concurrent.Semaphore rateLimiter = new java.util.concurrent.Semaphore(3);

    public CrossrefActivitiesImpl(RestClient crossrefRestClient) {
        this.crossrefRestClient = crossrefRestClient;
        java.util.concurrent.Executors.newScheduledThreadPool(1)
            .scheduleAtFixedRate(() -> {
                rateLimiter.release(3 - rateLimiter.availablePermits());
            }, 0, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Override
    public CitedReferencesResult getWorks(String doi) {
        System.out.println("CrossrefActivitiesImpl.getWorks called with DOI: " + doi);
        URI targetUrl = UriComponentsBuilder.fromUriString(BASE_URL)
            .path("/works/{doi}/transform")
            .buildAndExpand(doi)
            .toUri();
System.out.println("CrossrefActivitiesImpl.getWorks: targetUrl=" + targetUrl);  
        String xmlRawPayload = crossrefRestClient.get()
            .uri(targetUrl)
            .header("Accept","application/vnd.crossref.unixsd+xml")
            .retrieve()
            .body(String.class);
            try {
                // System.out.println("Raw XML payload received: " + xmlRawPayload); // Debug: Print raw XML response
                XmlMapper xmlMapper = new XmlMapper();
                CrossrefXmlResponse xmlResponse = xmlMapper.readValue(xmlRawPayload, CrossrefXmlResponse.class);
                return CitedReferencesResult.fromXml(doi, xmlRawPayload, xmlResponse);
            } catch (Exception e) {
                throw new RuntimeException("Jackson processing error on Crossref content", e);
            }
    }

    @Override
    public CitedReferencesResult getWorksBy(String citeKey, String title, String author) {
        System.out.println("CrossrefActivitiesImpl.getWorks called with title: " + title + ", author: " + author);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(BASE_URL).path("/works");

        if (title != null && !title.isBlank()) {
            uriBuilder.queryParam("query.title", title);
        }
        if (author != null && !author.isBlank()) {
            uriBuilder.queryParam("query.author", author);
        }
        try {
            // Smoothly paces requests to protect crossref.org and block teammate complaints
            rateLimiter.acquire(); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Crossref rate limiter interrupted", e);
        }

        URI targetUrl = uriBuilder.build().toUri();
System.out.println("CrossrefActivitiesImpl.getWorks: targetUrl=" + targetUrl);  
    java.time.LocalTime now = java.time.LocalTime.now();
    System.out.println(String.format(
        "[DRY RUN API HIT] Timestamp: %02d:%02d:%02d.%03d | Query Title: %s", 
        now.getHour(), now.getMinute(), now.getSecond(), now.get(java.time.temporal.ChronoField.MILLI_OF_SECOND),
        title
    ));
    System.out.println("CrossrefActivitiesImpl.getWorks: Sending request to targetUrl=" + targetUrl);
        String jsonPayload = crossrefRestClient.get()
            .uri(targetUrl)
            // .header("Accept", "application/vnd.crossref.unixsd+xml")
            .retrieve()
            .body(String.class);
            try{
                ObjectMapper jsonMapper = new ObjectMapper();
                CrossrefSearchResponse searchResponse = jsonMapper.readValue(jsonPayload, CrossrefSearchResponse.class);
                return CitedReferencesResult.fromJson(citeKey, jsonPayload, searchResponse);
            } catch (Exception e) {
                throw new RuntimeException("Error processing Crossref search response", e);
            }
    }

}
