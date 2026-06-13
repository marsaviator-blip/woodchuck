package org.woodchuck.zChecker.dtos;

import java.util.Map;

public class GraphGistDTO {
    private long totalNodes;
    private long totalEdges;
    private Map<String, Long> labels;

    public GraphGistDTO(long totalNodes, long totalEdges, Map<String, Long> labels) {
        this.totalNodes = totalNodes;
        this.totalEdges = totalEdges;
        this.labels = labels;
    }

    // Getters
    public long getTotalNodes() { return totalNodes; }
    public long getTotalEdges() { return totalEdges; }
    public Map<String, Long> getLabels() { return labels; }
}
