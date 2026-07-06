package org.woodchuck.zChecker.dtos;

import java.util.List;
import java.util.Map;

//public record GraphStats(Long totalNodes, Long totalEdges, List<String> nodeTypes) {}
public record GraphStats(
    Long totalNodes,
    Long totalEdges,
    List<String> labels,
    Map<String, Map<String, List<String>>> nodeSchemaMap,
    Map<String, Map<String, List<String>>> relSchemaMap
) {}

// old way
// public class GraphStats {
//     private final Long totalNodes;
//     private final Long totalEdges;
//     private final List<String> labels;
//     private final Map<String, Map<String, List<String>>> nodeSchemaMap;
//     private final Map<String, Map<String, List<String>>> relSchemaMap;

//     // Main Constructor
//     public GraphStats(Long totalNodes, 
//                       Long totalEdges, 
//                       List<String> labels, 
//                       Map<String, Map<String, List<String>>> nodeSchemaMap, 
//                       Map<String, Map<String, List<String>>> relSchemaMap) {
//         this.totalNodes = totalNodes;
//         this.totalEdges = totalEdges;
//         this.labels = labels;
//         this.nodeSchemaMap = nodeSchemaMap;
//         this.relSchemaMap = relSchemaMap;
//     }

//     // Getters required for Jackson JSON conversion
//     public Long getTotalNodes() { return totalNodes; }
//     public Long getTotalEdges() { return totalEdges; }
//     public List<String> getLabels() { return labels; }
//     public Map<String, Map<String, List<String>>> getNodeSchemaMap() { return nodeSchemaMap; }
//     public Map<String, Map<String, List<String>>> getRelSchemaMap() { return relSchemaMap; }
// }

