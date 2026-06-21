package org.woodchuck.zChecker.dtos;

import java.util.Map;
import java.util.List;

public class GraphGistDTO {
    private long totalNodes;
    private long totalEdges;
    private Map<String, Long> labels;
    private String propertyName;
    private List<String> propertyTypes;

    public GraphGistDTO(long totalNodes, long totalEdges, Map<String, Long> labels, String propertyName,
    List<String> propertyTypes) {
        this.totalNodes = totalNodes;
        this.totalEdges = totalEdges;
        this.labels = labels;
        this.propertyName = propertyName;
        this.propertyTypes = propertyTypes;
    }

    // Getters
    public long getTotalNodes() { return totalNodes; }
    public long getTotalEdges() { return totalEdges; }
    public Map<String, Long> getLabels() { return labels; }
    public String getPropertyName() { return propertyName; }
    public List<String> getPropertyTypes() { return propertyTypes; }
}
