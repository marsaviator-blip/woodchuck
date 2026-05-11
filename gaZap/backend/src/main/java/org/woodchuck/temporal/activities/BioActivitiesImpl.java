package org.woodchuck.temporal.activities;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.temporal.spring.boot.ActivityImpl;

@Component
@ActivityImpl(taskQueues = "BioTaskQueue")
public class BioActivitiesImpl implements BioActivities {
    private static final String BASE_SEARCH_URL = "https://search.rcsb.org";
    private static final String BASE_DATA_URL = "https://data.rcsb.org";

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BioActivitiesImpl(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public List<String> searchIdentifiers(String query) {
        try {
            URI targetUrl = UriComponentsBuilder.fromUriString(BASE_SEARCH_URL)
                .path("/rcsbsearch/v2/query")
                .query(query)
                .build(false)
                .toUri();
System.out.println("BioActivitiesImpl.searchIdentifiers: targetUrl=" + targetUrl);  
            String response = restClient.get()
                .uri(targetUrl)
                .retrieve()
                .body(String.class);

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode identifierNode = rootNode.path("result_set");
            List<String> identifiers = new ArrayList<>();

            for (JsonNode node : identifierNode) {
                JsonNode idNode = node.get("identifier");
                if (idNode != null) {
                    identifiers.add(idNode.asText());
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            ArrayNode structures = root.putArray("entries");
            structures.addAll((ArrayNode) mapper.valueToTree(identifiers));
    
            System.out.println("BioActivitiesImpl.searchIdentifiers: identifiers=" + root.toString());  
            return identifiers;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to fetch RCSB search identifiers", ex);
        }
    }

    @Override
    public List<String> fetchEntries(List<String> entries) {
        try {
            List<String> data = new ArrayList<>();
            for (String entry : entries) {
                URI targetUrl = UriComponentsBuilder.fromUriString(BASE_DATA_URL)
                    .path("/rest/v1/core/entry/{entry}")
                    .buildAndExpand(entry)
                    .toUri();

                String response = restClient.get()
                    .uri(targetUrl)
                    .retrieve()
                    .body(String.class);
                data.add(response);
                //System.out.println("BioActivitiesImpl.fetchEntries: entry=" + entry + ", response=" + response);
            JsonNode rootNode = objectMapper.readTree(response);
            if(rootNode.has("rcsb_external_references")){
                JsonNode externalReferenceNode = rootNode.get("rcsb_external_references");
                for(JsonNode refNode : externalReferenceNode){
                    if(refNode.has("id") && refNode.has("link") && refNode.has("type")){
                        String emid = refNode.get("id").asText();
                        System.out.println("BioActivitiesImpl.fetchEntries: entry=" + entry + ", EMID=" + emid);
                        String link = refNode.get("link").asText();
                        System.out.println("BioActivitiesImpl.fetchEntries: entry=" + entry + ", Link=" + link);
                        String type = refNode.get("type").asText();
                        System.out.println("BioActivitiesImpl.fetchEntries: entry=" + entry + ", Type=" + type);
                    }
                }

            }
            }
            return data;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to fetch RCSB entry data", ex);
        }
    }

}
