package org.lim.components;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.lim.services.GraphService;

@Component
public class ClientRunner implements CommandLineRunner {

    private final GraphService graphService;

    // Constructor injection
    public ClientRunner(GraphService graphService) {
        this.graphService = graphService;
    }

    public void createGraph() {
        System.out.println("Creating graph in Neo4j...");
        graphService.createGraph();
        System.out.println("Graph creation process completed.");
    }   

    @Override
    public void run(String... args) {
        createGraph();
    }
}
