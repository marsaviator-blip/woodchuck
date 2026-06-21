package org.woodchuck.zChecker.dtos;

import java.util.List;
import java.util.Map;

public record NetworkGraphPayload(List<VisNode> nodes, List<VisLink> links) {
public record VisNode(String id, String label, Map<String, Object> properties) {}
public record VisLink(String source, String target, String type) {}
}
