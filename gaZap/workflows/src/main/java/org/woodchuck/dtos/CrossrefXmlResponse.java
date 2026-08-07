package org.woodchuck.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "crossref_result", namespace = "http://www.crossref.org/qrschema/3.0")
public record CrossrefXmlResponse(
    @JacksonXmlProperty(localName = "query_result", namespace = "http://www.crossref.org/qrschema/3.0") 
    QueryResult queryResult
) {

    public static CrossrefSearchResponse toSearchResponse(CrossrefXmlResponse xmlResponse) {
        if (xmlResponse == null || xmlResponse.queryResult() == null ||
            xmlResponse.queryResult().body() == null ||
            xmlResponse.queryResult().body().query() == null ||
            xmlResponse.queryResult().body().query().doiRecord() == null ||
            xmlResponse.queryResult().body().query().doiRecord().crossref() == null ||
            xmlResponse.queryResult().body().query().doiRecord().crossref().journal() == null ||
            xmlResponse.queryResult().body().query().doiRecord().crossref().journal().journalArticle() == null) {
            return new CrossrefSearchResponse(null);
        }

        JournalArticle article = xmlResponse.queryResult().body().query().doiRecord().crossref().journal().journalArticle();
        String articleTitle = (article.titles() != null) ? article.titles().title() : null;

        List<CrossrefSearchResponse.WorkItem> workItems = new ArrayList<>();
        if (article.citationList() != null && article.citationList().citations() != null) {
            for (Citation citation : article.citationList().citations()) {
                if (citation == null) {
                    continue;
                }

                List<String> titles = articleTitle != null && !articleTitle.isBlank() ? List.of(articleTitle) : List.of();
                if (citation.unstructuredCitation() != null && !citation.unstructuredCitation().isBlank() && titles.isEmpty()) {
                    titles = List.of(citation.unstructuredCitation());
                }

                List<String> containerTitles = citation.journalTitle() != null && !citation.journalTitle().isBlank() ? List.of(citation.journalTitle()) : List.of();

                CrossrefSearchResponse.WorkItem item = new CrossrefSearchResponse.WorkItem(
                    citation.getDoiProperty(),
                    null, // type
                    null, // score
                    titles,
                    containerTitles,
                    null, // authors
                    null, // volume
                    null, // issue
                    null, // page
                    null, // issued
                    null  // references
                );
                workItems.add(item);
            }
        }

        CrossrefSearchResponse.MessageContainer messageContainer = new CrossrefSearchResponse.MessageContainer(workItems);
        return new CrossrefSearchResponse(messageContainer);
    }


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
            new QueryResult(
                new Body(
                    new Query(
                        new String(),
                        new DoiRecord(
                            new Crossref(
                                new Journal(
                                    new JournalArticle(
                                        new Titles(articleTitle),
                                        new CitationList(citations),
                                        new DoiData(safe(items.get(0).doi()))
                                    )
                                )
                            )
                        )
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
record QueryResult(
    @JacksonXmlProperty(localName = "body", namespace = "http://www.crossref.org/qrschema/3.0") 
    Body body
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Body(
    @JacksonXmlProperty(localName = "query", namespace = "http://www.crossref.org/qrschema/3.0") 
    Query query
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Query(
    @JacksonXmlProperty(localName = "doi", namespace = "http://www.crossref.org/qrschema/3.0") 
    String doi,

    @JacksonXmlProperty(localName = "doi_record", namespace = "http://www.crossref.org/qrschema/3.0") 
    DoiRecord doiRecord
) {
    String getDoi() {
        return doi;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
record DoiRecord(
    // NOTE: The namespace switches to 1.1 right here!
    @JacksonXmlProperty(localName = "crossref", namespace = "http://www.crossref.org/xschema/1.1") 
    Crossref crossref
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Crossref(
    @JacksonXmlProperty(localName = "journal", namespace = "http://www.crossref.org/xschema/1.1") 
    Journal journal
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Journal(
    @JacksonXmlProperty(localName = "journal_article", namespace = "http://www.crossref.org/xschema/1.1")
    JournalArticle journalArticle
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record JournalArticle(
    @JacksonXmlProperty(localName = "titles", namespace = "http://www.crossref.org/xschema/1.1") Titles titles,
    
    // Maps the outer <citation_list> container
    @JacksonXmlProperty(localName = "citation_list") CitationList citationList,
    
    @JacksonXmlProperty(localName = "doi_data") DoiData doiData
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
record DoiData(
    @JacksonXmlProperty(localName = "doi") String doi
) {
    String getDoi() {
        return doi;
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
record Citation(
    @JacksonXmlProperty(isAttribute = true, localName = "key") String key, 
    @JacksonXmlProperty(localName = "journal_title") String journalTitle,
    @JacksonXmlProperty(localName = "unstructured_citation") String unstructuredCitation,
    
    // The specific <doi> target property inside the reference item
    @JacksonXmlProperty(localName = "doi") String doiProperty 
) {
    String getDoiProperty() {
        return doiProperty;
    }
}

// // Handles the text value trapped inside the <doi></doi> element bounds
// @JsonIgnoreProperties(ignoreUnknown = true)
// record DoiProperty(
//     @JacksonXmlText String value
// ) {}
