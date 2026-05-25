package org.woodchuck.dtos;

public record DocumentAnalysisResult(
    String referenceSection, 
    BibliographyResponse bibliography
) {}
