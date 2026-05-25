package org.woodchuck.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "crossref_result")
public record CrossrefXmlResponse(
    @JacksonXmlProperty(localName = "body") Body body
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Body(
    @JacksonXmlProperty(localName = "journal") Journal journal
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Journal(
    @JacksonXmlProperty(localName = "journal_article") JournalArticle article
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record JournalArticle(
    @JacksonXmlProperty(localName = "titles") Titles titles,
    
    // Maps the outer <citation_list> container
    @JacksonXmlProperty(localName = "citation_list") CitationList citationList 
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Titles(
    @JacksonXmlProperty(localName = "title") String title
) {}

// Unwraps the repeated <citation> elements inside the list container
@JsonIgnoreProperties(ignoreUnknown = true)
record CitationList(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "citation")
    List<Citation> citations
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Citation(
    @JacksonXmlProperty(isAttribute = true, localName = "key") String key, 
    @JacksonXmlProperty(localName = "journal_title") String journalTitle,
    @JacksonXmlProperty(localName = "unstructured_citation") String unstructuredCitation,
    
    // The specific <doi> target property inside the reference item
    @JacksonXmlProperty(localName = "doi") String doiProperty 
) {}

// // Handles the text value trapped inside the <doi></doi> element bounds
// @JsonIgnoreProperties(ignoreUnknown = true)
// record DoiProperty(
//     @JacksonXmlText String value
// ) {}
