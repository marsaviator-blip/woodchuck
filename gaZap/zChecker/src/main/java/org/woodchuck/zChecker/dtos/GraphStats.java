package org.woodchuck.zChecker.dtos;

import java.util.List;

public record GraphStats(Long totalNodes, Long totalEdges, List<String> nodeTypes) {}

