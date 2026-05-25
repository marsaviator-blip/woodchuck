package org.woodchuck.dtos;

import java.util.List;

public record BibliographyResponse(List<Citation> citations) {

    public record Citation(String type, String citeKey, List<BibTeXField> fields,
        List<Citation> children) {

        public Citation(String type, String citeKey, List<BibTeXField> fields) {
            this(type, citeKey, fields, null);
        }

        public Citation withChildren(List<Citation> newChildren) {
            return new Citation(this.type, this.citeKey, this.fields, newChildren);
        }
  }
    
    public record BibTeXField(String key, String value) {}

}
