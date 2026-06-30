package org.woodchuck.zChecker.services;

import org.woodchuck.zChecker.dtos.AuthorPayloadDTO.AuthorInfo;
import org.woodchuck.zChecker.dtos.AuthorPayloadDTO.AuthorWithDocs;
import org.woodchuck.zChecker.dtos.AuthorPayloadDTO;
import org.woodchuck.zChecker.dtos.AuthorPayloadDTO.AuthorWithDocs.DocumentInfo;
import org.woodchuck.zChecker.dtos.GraphStats;
import org.woodchuck.zChecker.repository.GraphRepository;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.Record;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

@Service
public class GraphMetricsService {

    private static final Logger logger = LoggerFactory.getLogger(GraphMetricsService.class);

//    public record NodePropertySchema(List<String> labels, String propertyName, List<String> propertyTypes) {}
//    public record RelPropertySchema(String relType, String propertyName, List<String> propertyTypes) {}
    private record RawNodeProp(List<String> labels, String propName, List<String> types) {}
    private record RawRelProp(String relType, String propName, List<String> types) {}

    private final Neo4jClient neo4jClient;
    private final GraphRepository graphRepository;

    public GraphMetricsService(Neo4jClient neo4jClient, GraphRepository graphRepository) {
        this.neo4jClient = neo4jClient;
        this.graphRepository = graphRepository;
    }

        public void clearDatabase() {
        logger.warn("Initiating full database clear operation.");
        
        // Execute the repository method
        graphRepository.purgeEntireDatabase();
        
        logger.info("Database successfully cleared.");
    }

        public GraphStats getDatabaseCounts() {
        // High-performance metadata query
        String countsQuery = "CALL apoc.meta.stats() YIELD nodeCount, relCount " +
                             "RETURN nodeCount AS totalNodes, relCount AS totalEdges";

        String labelsQuery = "CALL db.labels() YIELD label RETURN label ORDER BY label ASC";

        String nodePropsQuery = "CALL db.schema.nodeTypeProperties() " +
                                "YIELD nodeLabels, propertyName, propertyTypes " +
                                "RETURN nodeLabels, propertyName, propertyTypes";

        String relPropsQuery = "CALL db.schema.relTypeProperties() " +
                               "YIELD relType, propertyName, propertyTypes " +
                               "RETURN relType, propertyName, propertyTypes";

        // 1. Fetch labels list
        List<String> labels = new ArrayList<>(
            neo4jClient.query(labelsQuery)
                .fetchAs(String.class)
                .all()
        );
        @SuppressWarnings("rawtypes")
        List<Map> rawNodeProps = new ArrayList<>(
            neo4jClient.query(nodePropsQuery)
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> {
                    var labelsVal = record.get("nodeLabels");
                    var nameVal = record.get("propertyName");
                    var typesVal = record.get("propertyTypes");

                    return Map.of(
                        "labels", (!labelsVal.isNull()) ? labelsVal.asList(v -> v.asString()) : List.of("Unknown"),
                        "name", (!nameVal.isNull()) ? nameVal.asString() : "unnamed",
                        "types", (!typesVal.isNull()) ? typesVal.asList(v -> v.asString()) : List.of("Unknown")
                    );
                })
                .all()
        );
        // @SuppressWarnings("rawtypes")
        // List<Map> rawNodeProps = new ArrayList<>(
        //     neo4jClient.query(nodePropsQuery)
        //         .fetchAs(Map.class)
        //         .mappedBy((typeSystem, record) -> Map.of(
        //             "labels", record.get("nodeLabels").asList(v -> v.asString()),
        //             "name", record.get("propertyName").asString(),
        //             "types", record.get("propertyTypes").asList(v -> v.asString())
        //         ))
        //         .all()
        // );
        @SuppressWarnings("rawtypes")
        List<Map> rawRelProps = new ArrayList<>(
            neo4jClient.query(relPropsQuery)
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> {
                    var relTypeVal = record.get("relType");
                    var nameVal = record.get("propertyName");
                    var typesVal = record.get("propertyTypes");

                    return Map.of(
                        "relType", (!relTypeVal.isNull()) ? relTypeVal.asString() : "UNKNOWN",
                        "name", (!nameVal.isNull()) ? nameVal.asString() : "unnamed",
                        "types", (!typesVal.isNull()) ? typesVal.asList(v -> v.asString()) : List.of("Unknown")
                    );
                })
                .all()
        );
        @SuppressWarnings("unchecked")
        Map<String, Map<String, List<String>>> nodeSchemaMap = rawNodeProps.stream()
            .flatMap(prop -> ((List<String>) prop.get("labels")).stream()
                .map(label -> Map.entry(label, Map.entry((String) prop.get("name"), (List<String>) prop.get("types")))))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.toMap(
                    e -> e.getValue().getKey(),
                    e -> e.getValue().getValue(),
                    (existing, replacement) -> existing
                )
            ));

        // 3. Fetch raw relationship properties 
        // List<RawRelProp> rawRelProps = new ArrayList<>(
        //     neo4jClient.query(relPropsQuery)
        //         .fetchAs(Map.class)
        //         .mappedBy((typeSystem, record) -> Map.of(
        //             record.get("relType").asString(),
        //             record.get("propertyName").asString(),
        //             record.get("propertyTypes").asList(v -> v.asString())
        //         ))
        //         .all()
        // );
        @SuppressWarnings("unchecked")
        Map<String, Map<String, List<String>>> relSchemaMap = rawRelProps.stream()
            .collect(Collectors.groupingBy(
                prop -> (String) prop.get("relType"),
                Collectors.toMap(
                    prop -> (String) prop.get("name"),
                    prop -> (List<String>) prop.get("types"),
                    (existing, replacement) -> existing
                )
            ));

        return neo4jClient.query(countsQuery)
            .fetchAs(Map.class)
            .mappedBy((typeSystem, record) -> Map.of(
                "totalNodes", record.get("totalNodes").asLong(0L),
                "totalEdges", record.get("totalEdges").asLong(0L)
            ))
            .one()
            .map(stats -> new GraphStats(
                (Long) stats.get("totalNodes"),
                (Long) stats.get("totalEdges"),
                labels,
                nodeSchemaMap,
                relSchemaMap
            ))
            .orElseGet(() -> new GraphStats(0L, 0L, new ArrayList<>(), new HashMap<>(), new HashMap<>()));
    }

// would not compile
// public GraphStats getDatabaseCounts() {
//     // 1. Fast metadata counts utilizing database statistics
//     String countsQuery = "CALL apoc.meta.stats() YIELD nodeCount, relCount " +
//                          "RETURN nodeCount AS totalNodes, relCount AS totalEdges";

//     String labelsQuery = "CALL db.labels() YIELD label RETURN label ORDER BY label ASC";

//     String nodePropsQuery = "CALL db.schema.nodeTypeProperties() " +
//                             "YIELD nodeLabels, propertyName, propertyTypes " +
//                             "RETURN nodeLabels, propertyName, propertyTypes";

//     String relPropsQuery = "CALL db.schema.relTypeProperties() " +
//                            "YIELD relType, propertyName, propertyTypes " +
//                            "RETURN relType, propertyName, propertyTypes";

//     // Fetch total labels list
//     List<String> labels = new ArrayList<>(
//         neo4jClient.query(labelsQuery).fetchAs(String.class).all()
//     );

//     // Fetch raw node properties from transactional schema
//     List<RawNodeProp> rawNodeProps = neo4jClient.query(nodePropsQuery)
//         .fetch()
//         .mappedBy((typeSystem, rec) -> new RawNodeProp(
//             rec.get("nodeLabels").asList(v -> v.asString()),
//             rec.get("propertyName").asString(),
//             rec.get("propertyTypes").asList(v -> v.asString())
//         )).all();

//     // Fetch raw relationship properties from transactional schema
//     List<RawRelProp> rawRelProps = neo4jClient.query(relPropsQuery)
//         .fetch()
//         .mappedBy((typeSystem, rec) -> new RawRelProp(
//             rec.get("relType").asString(),
//             rec.get("propertyName").asString(),
//             rec.get("propertyTypes").asList(v -> v.asString())
//         )).all();

//     // Group Node schema: Map<Label, Map<PropertyName, List<PropertyType>>>
//     Map<String, Map<String, List<String>>> nodeSchemaMap = rawNodeProps.stream()
//         .flatMap(prop -> prop.labels().stream()
//             .map(label -> Map.entry(label, Map.entry(prop.propName(), prop.types()))))
//         .collect(Collectors.groupingBy(
//             Map.Entry::getKey,
//             Collectors.toMap(
//                 e -> e.getValue().getKey(),
//                 e -> e.getValue().getValue(),
//                 (existing, replacement) -> existing // Merge strategy for duplicate schema entries
//             )
//         ));

//     // Group Relationship schema: Map<RelationshipType, Map<PropertyName, List<PropertyType>>>
//     Map<String, Map<String, List<String>>> relSchemaMap = rawRelProps.stream()
//         .collect(Collectors.groupingBy(
//             RawRelProp::relType,
//             Collectors.toMap(
//                 RawRelProp::propName,
//                 RawRelProp::types,
//                 (existing, replacement) -> existing
//             )
//         ));

//     // Execute global metrics counts and build the final GraphStats object
//     return neo4jClient.query(countsQuery)
//         .fetch()
//         .mappedBy((typeSystem, record) -> new GraphStats(
//             record.get("totalNodes").asLong(0L),
//             record.get("totalEdges").asLong(0L),
//             labels,
//             nodeSchemaMap,
//             relSchemaMap
//         ))
//         .one()
//         .orElseGet(() -> new GraphStats(0L, 0L, new ArrayList<>(), new HashMap<>(), new HashMap<>()));
// }


// public GraphStats getDatabaseCounts() {
//     // 1. Fast O(1) metadata count utilizing internal DB statistics
//     String countsQuery = "CALL apoc.meta.stats() YIELD nodeCount, relCount " +
//                          "RETURN nodeCount AS totalNodes, relCount AS totalEdges";

//     // 2. Optimized built-in label retrieval
//     String labelsQuery = "CALL db.labels() YIELD label RETURN label ORDER BY label ASC";

//     // 3. Node property keys and types schema query
//     String nodePropsQuery = "CALL db.schema.nodeTypeProperties() " +
//                             "YIELD nodeLabels, propertyName, propertyTypes " +
//                             "RETURN nodeLabels, propertyName, propertyTypes";

//     // 4. Relationship property keys and types schema query
//     String relPropsQuery = "CALL db.schema.relTypeProperties() " +
//                            "YIELD relType, propertyName, propertyTypes " +
//                            "RETURN relType, propertyName, propertyTypes";

//     // Fetch distinct string labels list
//     List<String> labels = new ArrayList<>(
//         neo4jClient.query(labelsQuery).fetchAs(String.class).all()
//     );

//     // Fetch node structural schemas
//     List<NodePropertySchema> nodeSchemas = new ArrayList<>(
//         neo4jClient.query(nodePropsQuery)
//             .fetch()
//             .mappedBy((typeSystem, record) -> new NodePropertySchema(
//                 record.get("nodeLabels").asList(v -> v.asString()),
//                 record.get("propertyName").asString(),
//                 record.get("propertyTypes").asList(v -> v.asString())
//             ))
//             .all()
//     );

//     // Fetch relationship structural schemas
//     List<RelPropertySchema> relSchemas = new ArrayList<>(
//         neo4jClient.query(relPropsQuery)
//             .fetch()
//             .mappedBy((typeSystem, record) -> new RelPropertySchema(
//                 record.get("relType").asString(),
//                 record.get("propertyName").asString(),
//                 record.get("propertyTypes").asList(v -> v.asString())
//             ))
//             .all()
//     );

//     // Execute O(1) global count query and pass everything down to GraphStats
//     return neo4jClient.query(countsQuery)
//         .fetch()
//         .mappedBy((typeSystem, record) -> new GraphStats(
//             record.get("totalNodes").asLong(0L),
//             record.get("totalEdges").asLong(0L),
//             labels,
//             nodeSchemas,
//             relSchemas
//         ))
//         .one()
//         .orElseGet(() -> new GraphStats(0L, 0L, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
// }

// public GraphStats getDatabaseCounts() {
//     // Fast O(1) metadata count utilizing internal DB statistics
//     String countsQuery = "CALL apoc.meta.stats() YIELD nodeCount, relCount " +
//                          "RETURN nodeCount AS totalNodes, relCount AS totalEdges";

//     // Optimized built-in label retrieval
//     String labelsQuery = "CALL db.labels() YIELD label RETURN label ORDER BY label ASC";

//     // Fetch labels list safely
//     List<String> labels = new ArrayList<>(
//         neo4jClient.query(labelsQuery)
//             .fetchAs(String.class)
//             .all()
//     );

//     // Execute counts and map to GraphStats object
//     return neo4jClient.query(countsQuery)
//         .fetch()
//         .mappedBy((typeSystem, record) -> mapToGraphStats(record, labels))
//         .one()
//         .orElseGet(() -> new GraphStats(0L, 0L, new ArrayList<>()));
// }

// Extracted mapping function to keep code clean and maintainable
// private GraphStats mapToGraphStats(Record record, List<String> labels) {
//     long totalNodes = record.get("totalNodes").asLong(0L);
//     long totalEdges = record.get("totalEdges").asLong(0L);
    
//     return new GraphStats(totalNodes, totalEdges, labels);
// }


    // public GraphStats getDatabaseCounts() {
    //     String countsQuery = """
    //         MATCH (n)
    //         WITH count(n) AS nodeCount
    //         MATCH ()-[r]->()
    //         RETURN nodeCount AS totalNodes, count(r) AS totalEdges
    //         """;

    //     String labelsQuery = "CALL db.labels() YIELD label RETURN label ORDER BY label ASC";
    //     String propertyQuery = "CALL db.schema.nodeTypeProperties() "+ 
    //         "YIELD nodeType, nodeLabels, propertyName, propertyTypes "+
    //         "RETURN nodeLabels, propertyName, propertyTypes";
    //     String relationQuery = "CALL db.schema.relTypeProperties() "+
    //         "YIELD relType, propertyName, propertyTypes "+
    //         "RETURN relType, propertyName, propertyTypes";

    //     List<String> labels = new ArrayList<>(
    //         neo4jClient.query(labelsQuery)
    //             .fetchAs(String.class)
    //             .all()
    //     );

    //     return neo4jClient.query(countsQuery)
    //             .fetchAs(GraphStats.class)
    //             .mappedBy((typeSystem, record) -> new GraphStats(
    //                     record.get("totalNodes").asLong(),
    //                     record.get("totalEdges").asLong(),
    //                     labels,
    //                     record.get("propertyName").asString(),
    //                     record.get("propertyTypes").asList(value -> value.asString())
    //             )
    //             .one()
    //             .orElse(new GraphStats(0L, 0L, new ArrayList<>(), "no data", new ArrayList<>()));
    // }

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

    public AuthorPayloadDTO getAuthorDetails() {
        // Query 1: Get single numeric count of Author nodes
        String countQuery = "MATCH (a:Author) RETURN count(a) AS totalAuthors";
        Long authorCount = neo4jClient.query(countQuery)
                .fetchAs(Long.class)
                .one()
                .orElse(0L);

        // Query 2: Get full collection of Author nodes
        String listQuery = "MATCH (a:Author) RETURN a.id AS id, a.name AS name ORDER BY a.name ASC";
        List<AuthorPayloadDTO.AuthorInfo> authorList = neo4jClient.query(listQuery)
                .fetchAs(AuthorInfo.class)
                .mappedBy((typeSystem, record) -> new AuthorInfo(
                        record.get("id").asString(),
                        record.get("name").asString()
                ))
                .all().stream().toList();

        // Query 3: Complex path returning Authors and their related Documents collected as a list
        String relationshipsQuery = """
            MATCH (a:Author)
            OPTIONAL MATCH (a)-[:WROTE|HAS_DOC]->(d:Document)
            RETURN a.id AS authorId, a.name AS authorName, 
                   collect({id: d.id, title: d.title}) AS docs
            ORDER BY authorName ASC
            """;

        List<AuthorPayloadDTO.AuthorWithDocs> authorsWithDocs = neo4jClient.query(relationshipsQuery)
                .fetch()
                .all()
                .stream()
                .map(row -> {
                    String authorId = (String) row.get("authorId");
                    String authorName = (String) row.get("authorName");
                    
                    // Parse out the nested collection maps safely 
                    List<Map<String, Object>> rawDocs = (List<Map<String, Object>>) row.get("docs");
                    List<AuthorPayloadDTO.AuthorWithDocs.DocumentInfo> docList = new ArrayList<>();
                    
                    if (rawDocs != null) {
                        for (Map<String, Object> docMap : rawDocs) {
                            // Filter out null rows caused by the OPTIONAL MATCH path when author has 0 docs
                            if (docMap.get("id") != null) {
                                docList.add(new AuthorPayloadDTO.AuthorWithDocs.DocumentInfo(
                                    (String) docMap.get("id"),
                                    (String) docMap.get("title")
                                ));
                            }
                        }
                    }
                    return new AuthorPayloadDTO.AuthorWithDocs(authorId, authorName, docList);
                })
                .toList();

        return new AuthorPayloadDTO(authorCount, authorList, authorsWithDocs);
    }
}

