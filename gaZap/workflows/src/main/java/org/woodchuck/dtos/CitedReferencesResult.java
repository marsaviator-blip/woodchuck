package org.woodchuck.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

import org.woodchuck.dtos.CrossrefSearchResponse.ReferenceItem;
import org.woodchuck.dtos.CrossrefSearchResponse.WorkItem;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CitedReferencesResult(String citeKey,
    String rawXml,
    String rawJson,
    List<String> extractedDois) {

     // Compact constructor to auto-populate extractedDois dynamically
    public CitedReferencesResult {
        // Ensure lists are always immutable and never null
        if (extractedDois == null) {
            extractedDois = List.of();
        }
    }

    public String getRawXml() {
        return rawXml;
    }

    public String getRawJson() {
        return rawJson;
    }
    public List<String> getExtractedReferenceDois() {
        return extractedDois;
    }

    // Static Factory Method for JSON Pipeline
    public static CitedReferencesResult fromJson(String citeKey, String rawJson, CrossrefSearchResponse searchResponse) {
        List<String> dois = extractFromJson(searchResponse);
        return new CitedReferencesResult(citeKey, null, rawJson, dois);
    }

    // Static Factory Method for XML Pipeline
    public static CitedReferencesResult fromXml(String citeKey, String rawXml, CrossrefXmlResponse xmlResponse) {
        List<String> dois = extractFromXml(xmlResponse);
        return new CitedReferencesResult(citeKey, rawXml, null, dois);
    }

    // Helper method to parse JSON payloads
    private static List<String> extractFromJson(CrossrefSearchResponse searchResponse) {
        if (searchResponse == null || searchResponse.message() == null || searchResponse.message().items() == null) {
            return List.of();
        }
        List<String> collectedDois = new ArrayList<>();
        for (WorkItem item : searchResponse.message().items()) {
            if (item.references() != null) {
                for (ReferenceItem ref : item.references()) {
                    if (ref.doi() != null && !ref.doi().isEmpty()) {
                        collectedDois.add(ref.doi());
                    }
                }
            } else if (item.doi() != null && !item.doi().isEmpty()) {
                collectedDois.add(item.doi());
            }
        }
        return List.copyOf(collectedDois); // Returns unmodifiable list
    }

    // Helper method to parse XML payloads
    private static List<String> extractFromXml(CrossrefXmlResponse xmlResponse) {
        if (xmlResponse == null || xmlResponse.queryResult() == null || xmlResponse.queryResult().body() == null 
            || xmlResponse.queryResult().body().query() == null) {
            return List.of();
        }
        
        List<String> collectedDois = new ArrayList<>();
        
        // 1. Direct path: Pull target DOI from the flat outer query tag first
        String targetDoi = xmlResponse.queryResult().body().query().getDoi();
        if (targetDoi != null && !targetDoi.isEmpty()) {
            collectedDois.add(targetDoi);
        }

        // 2. Safe polymorphic fallback path using Java Optional chains to prevent NPEs
        if (collectedDois.isEmpty()) {
            java.util.Optional.of(xmlResponse)
                .map(CrossrefXmlResponse::queryResult)
                .map(QueryResult::body)
                .map(Body::query)
                .map(Query::doiRecord)
                .map(DoiRecord::crossref)
                .map(Crossref::journal)          // 👈 If journal is null, evaluation safely halts here
                .map(Journal::journalArticle)
                .map(JournalArticle::doiData)
                .map(DoiData::getDoi)
                .filter(doi -> !doi.isEmpty())
                .ifPresent(collectedDois::add);
        }
        
        return List.copyOf(collectedDois);
    }
}
