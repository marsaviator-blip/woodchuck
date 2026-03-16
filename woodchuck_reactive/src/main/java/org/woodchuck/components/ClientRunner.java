package org.woodchuck.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.woodchuck.services.WebClientMPClient;

@ComponentYamlToBolt;
import org.woodchuck.dtos.MaterialStructureParams;
import org.woodchuck.services.MPService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class ClientRunner implements CommandLineRunner {

    private final MPService mpService;

    // Constructor injection
    public ClientRunner(MPService mpService) {
        this.mpService = mpService;
    }

    public void fetchChemicalElement(String element) {
        System.out.println("Fetching chemical element data for: " + element);

        String jsonString = mpService.getChemicalElement(element); // Fetch and print chemical element data
        System.out.println("Tried fetching chemical element data for: " + element);
        if (jsonString != null && !jsonString.isEmpty()) {
            System.out.println("Data fetched successfully:");
            System.out.println(jsonString);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode materialsNode = rootNode.path("data"); // Get the named array
            YamlToBolt structureToBolt = new YamlToBolt