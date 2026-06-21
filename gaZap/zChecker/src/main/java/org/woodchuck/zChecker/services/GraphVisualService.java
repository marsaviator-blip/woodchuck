package org.woodchuck.zChecker.services;

import org.woodchuck.zChecker.dtos.NetworkGraphPayload;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphVisualService {

    private final Neo4jClient neo4jClient;

    public GraphVisualService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public NetworkGraphPayload fetchSubGraphTopology(int elementLimit) {
        // Query limits relationship fetches to prevent browser rendering crashes
        String cypherQuery = "MATCH (s)-[r]->(t) " +
                             "RETURN s, r, t LIMIT $limit";

        Map<String, NetworkGraphPayload.VisNode> uniqueNodes = new LinkedHashMap<>();
        List<NetworkGraphPayload.VisLink> relationshipLinks = new ArrayList<>();

        neo4jClient.query(cypherQuery)
            .bind(elementLimit).to("limit")
            .fetchAs(Map.class)
            .mappedBy((typeSystem, record) -> {
                // Extract Neo4j Driver internal Map structures
                var sourceNode = record.get("s").asNode();
                var targetNode = record.get("t").asNode();
                var relEdge = record.get("r").asRelationship();

                String sourceId = String.valueOf(sourceNode.id());
                String targetId = String.valueOf(targetNode.id());

                // Parse and map Source Node if missing from map cache
                if (!uniqueNodes.containsKey(sourceId)) {
                    uniqueNodes.put(sourceId, new NetworkGraphPayload.VisNode(
                        sourceId,
                        sourceNode.labels().iterator().hasNext() ? sourceNode.labels().iterator().next() : "Node",
                        new HashMap<>(sourceNode.asMap())
                    ));
                }

                // Parse and map Target Node if missing from map cache
                if (!uniqueNodes.containsKey(targetId)) {
                    uniqueNodes.put(targetId, new NetworkGraphPayload.VisNode(
                        targetId,
                        targetNode.labels().iterator().hasNext() ? targetNode.labels().iterator().next() : "Node",
                        new HashMap<>(targetNode.asMap())
                    ));
                }

                // Store relationship topology links pointers
                relationshipLinks.add(new NetworkGraphPayload.VisLink(sourceId, targetId, relEdge.type()));
                return Map.of("processed", true);
            })
            .all();

        return new NetworkGraphPayload(new ArrayList<>(uniqueNodes.values()), relationshipLinks);
    }
}

