package org.woodchuck.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "crossref_result")
public record CrossrefXmlResponse(
    @JacksonXmlProperty(localName = "body") Body body
) {

    public static CrossrefXmlResponse fromSearchResponse(CrossrefSearchResponse searchResponse) {
        List<CrossrefSearchResponse.WorkItem> items = Collections.emptyList();
        if (searchResponse != null && searchResponse.message() != null && searchResponse.message().items() != null) {
            items = searchResponse.message().items();
        }

        String articleTitle = "";
        if (!items.isEmpty() && items.get(0) != null) {
            articleTitle = safe(items.get(0).getFirstTitle());
        }

        List<Citation> citations = new ArrayList<>();
        int keyIndex = 1;
        for (CrossrefSearchResponse.WorkItem item : items) {
            if (item == null) {
                continue;
            }

            citations.add(new Citation(
                "ref-" + keyIndex++,
                safe(item.getFirstJournal()),
                buildUnstructuredCitation(item),
                safe(item.doi())
            ));
        }

        return new CrossrefXmlResponse(
            new Body(
                new Journal(
                    new JournalArticle(
                        new Titles(articleTitle),
                        new CitationList(citations)
                    )
                )
            )
        );
    }

    private static String buildUnstructuredCitation(CrossrefSearchResponse.WorkItem item) {
        List<String> parts = new ArrayList<>();
        String title = safe(item.getFirstTitle());
        String journal = safe(item.getFirstJournal());
        String year = (item.issued() != null) ? safe(item.issued().getYear()) : "";

        if (!title.isBlank()) {
            parts.add(title);
        }
        if (!journal.isBlank()) {
            parts.add(journal);
        }
        if (!year.isBlank()) {
            parts.add(year);
        }

        return String.join(". ", parts);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}

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
