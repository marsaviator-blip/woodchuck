package org.lim.services;

import org.lim.components.GraphConfig;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;


@Service
public class GraphService {

    private final GraphConfig graphConfig;

    public GraphService(GraphConfig graphConfig) {
        this.graphConfig = graphConfig;
    }  

    @PostConstruct
    public void init() {
        System.out.println("GraphService initialized with person: " + graphConfig.getPerson());
        //System.out.println("Interests: " + graphConfig.getInterests());
    }

    public void createGraph() {
        // Logic to create graph in Neo4j using the Neo4jClient
    }

}