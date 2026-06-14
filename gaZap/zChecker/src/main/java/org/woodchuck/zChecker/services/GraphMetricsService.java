package org.woodchuck.zChecker.services;

import org.woodchuck.zChecker.dtos.GraphStats;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GraphMetricsService {

    private final Neo4jClient neo4jClient;

    public GraphMetricsService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public GraphStats getDatabaseCounts() {
        String countsQuery = """
            MATCH (n)
            WITH count(n) AS nodeCount
            MATCH ()-[r]->()
            RETURN nodeCount AS totalNodes, count(r) AS totalEdges
            """;

        String labelsQuery = "CALL db.labels() YIELD label RETURN label ORDER BY label ASC";

        List<String> labels = new ArrayList<>(
            neo4jClient.query(labelsQuery)
                .fetchAs(String.class)
                .all()
        );

        return neo4jClient.query(countsQuery)
                .fetchAs(GraphStats.class)
                .mappedBy((typeSystem, record) -> new GraphStats(
                        record.get("totalNodes").asLong(),
                        record.get("totalEdges").asLong(),
                        labels
                ))
                .one()
                .orElse(new GraphStats(0L, 0L, new ArrayList<>()));
    }

    // alternative
    // public GraphStats getFastDatabaseCounts() {
    //     // Uses Neo4j internal statistics instead of scanning the full graph
    //     String cypherQuery = "CALL apoc.meta.stats() YIELD nodeCount, relCount RETURN nodeCount AS totalNodes, relCount AS totalEdges";
        
    //     // Note: Requires APOC plugin enabled on your Neo4j Instance
    //     return neo4jClient.query(cypherQuery)
    //             .fetchAs(GraphStats.class)
    //             .mappedBy((typeSystem, record) -> new GraphStats(
    //                     record.get("totalNodes").asLong(),
    //                     record.get("totalEdges").asLong()
    //             ))
    //             .one()
    //             .orElse(new GraphStats(0L, 0L));
    // }

}

