package org.lim.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

    @Value("classpath:personInterest.yaml")
    public void createGraph() {
        // Logic to create graph in Neo4j using the Neo4jClient
    }

}