package org.woodchuck.temporal.activities;

import java.net.URI;

import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.stereotype.Component;
import io.temporal.spring.boot.ActivityImpl;

@Component
@ActivityImpl(taskQueues = "IngestionQueue")
public class UrlDocumentActivitiesImpl implements UrlDocumentActivities {

    private final RestClient restClient;

    public UrlDocumentActivitiesImpl(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public byte[] fetch(String url) {
        URI targetUrl = UriComponentsBuilder.fromUriString(url)
            .build()
            .toUri();
        return restClient.get()
        .uri(targetUrl)
        .retrieve()
        .body(byte[].class);
    }

}
