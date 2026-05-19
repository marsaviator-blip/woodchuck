package org.woodchuck.temporal.activities;

import java.net.URI;

import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Component;
import io.temporal.spring.boot.ActivityImpl;

@Component
@ActivityImpl(taskQueues = "CrossrefQueue")
public class CrossrefActivitiesImpl implements CrossrefActivities {

    private static final String BASE_URL = "https://api.crossref.org";

    private final RestClient restClient;

    public CrossrefActivitiesImpl(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public String getWorks(String doi) {
        System.out.println("CrossrefActivitiesImpl.getWorks called with DOI: " + doi);
        URI targetUrl = UriComponentsBuilder.fromUriString(BASE_URL)
            .path("/works/{doi}")
            .buildAndExpand(doi)
            .toUri();
System.out.println("CrossrefActivitiesImpl.getWorks: targetUrl=" + targetUrl);  
        return restClient.get()
            .uri(targetUrl)
            .retrieve()
            .body(String.class);

    }

}
