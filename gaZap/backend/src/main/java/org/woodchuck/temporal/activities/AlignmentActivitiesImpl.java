package org.woodchuck.temporal.activities;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.spring.boot.ActivityImpl;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.springframework.web.client.RestClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.client.MultipartBodyBuilder;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(taskQueues = "ALIGNMENT_QUEUE")
public class AlignmentActivitiesImpl implements AlignmentActivities {
    private final String BASE_URL = "https://alignment.rcsb.org";

    private final RestClient restClient;
    public AlignmentActivitiesImpl(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public String execute(List<String> entries) {
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        // Implementation of the alignment activity
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ObjectNode context = root.putObject("context");
        context.put("mode", "pairwise");
        context.putObject("method").put("name", "fatcat-rigid");
        ArrayNode structures = context.putArray("structures");

        for (int i = 0; i < entries.size(); i++) {
            ObjectNode struct = structures.addObject();
                struct.put("entry_id", entries.get(i));
                struct.putObject("selection").put("asym_id", "");
        }

        System.out.println("Executing alignment with input: " + root.toString());
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        // MyMetadata metadata = new MyMetadata("query", "example-desc");
        // builder.part("metadata", metadata, MediaType.APPLICATION_JSON);

        // Add a file part
        builder.part("query", root.toString(), MediaType.APPLICATION_JSON);


         URI targetUrl = UriComponentsBuilder.fromUriString(BASE_URL)
            .path("/api/v1/structures/submit")
             .build(false)
            .toUri();
System.out.println("AlignmentActivitiesImpl.execute: targetUrl=" + targetUrl);  
        String ticket = restClient.post()
            .uri(targetUrl)
            .contentType(MediaType.MULTIPART_MIXED)
            .body(builder.build())
            .retrieve()
            .body(String.class);
            System.out.println("AlignmentActivitiesImpl.execute: ticket=" + ticket);
         URI queryTargetUrl = UriComponentsBuilder.fromUriString(BASE_URL)
            .path("/api/v1/structures/results")
            .query("uuid=" + ticket)
             .build(false)
            .toUri();
System.out.println("AlignmentActivitiesImpl.execute: queryTargetUrl=" + queryTargetUrl);

        while (true) {
            try {
                ResponseEntity<String> response=restClient.get()
                .uri(queryTargetUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    ObjectMapper resultsMapper = new ObjectMapper();
                    ObjectNode responseJson = (ObjectNode) resultsMapper.readTree(response.getBody());
                    String status = responseJson.path("info").path("status").asText();
                    System.out.println(response.getBody()+" AlignmentActivitiesImpl.execute: poll response /results status=" + status);
                    if(status.equalsIgnoreCase("COMPLETE")) {
                        return response.getBody();
                    }
                }

                System.out.println("AlignmentActivitiesImpl.execute: poll response status=" + response.getStatusCode());
                ctx.heartbeat(response.getStatusCode().toString());
            } catch (Exception e) {
                // Log the error and let the activity retry, or heartbeat the failure
                System.err.println("Error polling alignment results: " + e.getMessage());
                ctx.heartbeat("Error: " + e.getMessage());
            }
            // 3. Wait before the next poll
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.err.println("Poll interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                throw new RuntimeException("Poll interrupted", e);
            }
        }
    }

}
