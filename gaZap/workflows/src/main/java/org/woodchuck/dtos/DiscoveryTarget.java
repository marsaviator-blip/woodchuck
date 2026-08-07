package org.woodchuck.dtos;

public record DiscoveryTarget(
    String parentDoi,
    String doi, 
    String author, 
    String title, 
    int generation
) {

}
