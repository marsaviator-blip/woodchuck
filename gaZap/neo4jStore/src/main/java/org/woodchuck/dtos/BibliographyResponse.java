package org.woodchuck.dtos;

import java.util.List;

public record BibliographyResponse(List<Citation> citations) {

    public record Citation(String type, String citeKey, List<BibTeXField> fields) {}
    
    public record BibTeXField(String key, String value) {}

}
