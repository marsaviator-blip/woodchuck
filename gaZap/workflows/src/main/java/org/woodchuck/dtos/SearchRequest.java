package org.woodchuck.dtos;

// What Gemini passes to your Java application
public record SearchRequest(String subjectQuery, int maxLinksToReturn) {}
