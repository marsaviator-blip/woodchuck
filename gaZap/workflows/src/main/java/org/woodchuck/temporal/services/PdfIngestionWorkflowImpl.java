package org.woodchuck.temporal.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import io.temporal.workflow.Promise;

import org.woodchuck.temporal.activities.UrlDocumentActivities;
import org.woodchuck.temporal.activities.CrossrefActivities;
import org.woodchuck.temporal.activities.PdfIngestionActivities;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import org.woodchuck.temporal.workflows.PdfIngestionWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Async;
import org.springframework.stereotype.Service;
import org.woodchuck.dtos.BibliographyResponse;
import org.woodchuck.dtos.CitedReferencesResult;
import org.woodchuck.dtos.CrossrefSearchResponse;
import org.woodchuck.dtos.CrossrefXmlResponse;
import org.woodchuck.dtos.DocumentAnalysisResult;
import org.woodchuck.services.VSmessageSender;

@Service
@WorkflowImpl(taskQueues = "IngestionQueue")
public class PdfIngestionWorkflowImpl implements PdfIngestionWorkflow {

    private ActivityExecutionSettings settings= new ActivityExecutionSettings();

    private VSmessageSender messageSender;
    private UrlDocumentActivities urlDocumentActivities;
    private PdfIngestionActivities pdfActivities;
    private CrossrefActivities crossrefActivities;

    PdfIngestionWorkflowImpl(VSmessageSender messageSender) {
        this.messageSender = messageSender;
    }
                    
    @Override
    public void execute(String url) {
        urlDocumentActivities = Workflow.newActivityStub(
            UrlDocumentActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(settings.getTimeoutSeconds()))
                     .setRetryOptions(
                        RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(settings.getInitialIntervalSeconds()))
                            .setBackoffCoefficient(settings.getBackoffCoefficient())
                            .setMaximumInterval(Duration.ofSeconds(settings.getMaximumIntervalSeconds()))
                            .setMaximumAttempts(settings.getMaximumAttempts())
                            .build())
                    .build());
        pdfActivities = Workflow.newActivityStub(
            PdfIngestionActivities.class,
            ActivityOptions.newBuilder()
                    //.withTemperature(0.7) // Keep it highly focused to prevent looping
                    .setStartToCloseTimeout(Duration.ofSeconds(60))//settings.getTimeoutSeconds()))
                     .setRetryOptions(
                        RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(settings.getInitialIntervalSeconds()))
                            .setBackoffCoefficient(settings.getBackoffCoefficient())
                            .setMaximumInterval(Duration.ofSeconds(settings.getMaximumIntervalSeconds()))
                            .setMaximumAttempts(settings.getMaximumAttempts())
                            .build())
                    .build());

        crossrefActivities = Workflow.newActivityStub(
            CrossrefActivities.class,
            ActivityOptions.newBuilder()
                    .setTaskQueue("CrossrefQueue")
                    .setStartToCloseTimeout(Duration.ofSeconds(settings.getTimeoutSeconds()))
                     .setRetryOptions(
                        RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(settings.getInitialIntervalSeconds()))
                            .setBackoffCoefficient(settings.getBackoffCoefficient())
                            .setMaximumInterval(Duration.ofSeconds(settings.getMaximumIntervalSeconds()))
                            .setMaximumAttempts(settings.getMaximumAttempts())
                            .build())
                    .build());

        byte[] rawPdfBytes = urlDocumentActivities.fetch(url);  
        DocumentAnalysisResult analysisResult = pdfActivities.extractReferenceSection(rawPdfBytes);
        DocumentAnalysisResult extractResults = pdfActivities.extractReferences(analysisResult);
        List<Promise<CrossrefXmlResponse>> crossRefPromises = new ArrayList<>();
        List<Promise<CitedReferencesResult>> crossRefSearchPromises = new ArrayList<>();

        extractResults.bibliography().citations().forEach(citation -> {
            System.out.println("Extracted citation: " + citation.type()+" "+citation.citeKey()); // Debug: Print each extracted citation
            citation.children().forEach(child -> {
            String title = child.fields().stream()
                    .filter(field -> field.key().equalsIgnoreCase("title"))
                    .findFirst()
                    .map(field -> field.value())
                    .orElse("Unknown Title");
            String author = child.fields().stream()
                .filter(field -> field.key().equalsIgnoreCase("author"))
                .findFirst()
                .map(field -> field.value())
                .orElse("Unknown Author");
            String date = child.fields().stream()
                .filter(field -> field.key().equalsIgnoreCase("date"))
                .findFirst()
                .map(field -> field.value())
                .orElse("Unknown Date");
            System.out.println("  Title: " + title); // Debug: Print the title of
            System.out.println("  Author: " + author); // Debug: Print the author of
            System.out.println("  Date: " + date); // Debug: Print the date? of
                String extractedDoi = helperExtractDoiRegex(title);
                if (extractedDoi != null) {
                    // Async execution: Schedules activity immediately without waiting for it to finish
                    Promise<CrossrefXmlResponse> promise = Async.function(crossrefActivities::getWorks, extractedDoi);
                    crossRefPromises.add(promise);
                }
                else {
                    // Handle lines where no DOI could be extracted
                    Promise<CitedReferencesResult> promise = Async.function(crossrefActivities::getWorksBy, child.citeKey(), title, author);
                    crossRefSearchPromises.add(promise);
                    
                }
            });
        });

        for (Promise<CitedReferencesResult> promise : crossRefSearchPromises) {
            try {
                CitedReferencesResult result = promise.get(); // Retrieves result or catches exception if single activity errored
                String citeKey = result.citeKey();
                CrossrefSearchResponse searchResponse = result.crossrefSearchResponse();
                System.out.println("Crossref search result for citeKey " + citeKey ); // Debug: Print the search response for each citeKey
                if (result != null && extractResults.bibliography().citations() != null) {
                    extractResults.bibliography().citations().get(0).children().stream()
                        .filter(citation -> citation.citeKey().equals(citeKey))
                        .findFirst()
                        .ifPresent(citation -> {
                            // Here you can enrich the citation with the search response data as needed
                            System.out.println("Enriching citation " + citeKey ); // Debug: Print enrichment step
                            // Example: You could add a new field to the citation or log the enrichment
                            searchResponse.message().items().forEach(item -> {
                                 System.out.println("  Found item in search response: DOI=" + item.doi() + ", Type=" + item.type()); // Debug: Print each item found in the search response
                            String enrichedType = item.type() != null ? item.type() : "";
                            String enrichedDoi = item.doi() != null ? item.doi() : "Unknown DOI";
                            String enrichedDate = item.issued() != null ? item.issued().getYear() : "";
                            List<BibliographyResponse.BibTeXField> enrichedFields = new ArrayList<>(citation.fields());
                            enrichedFields.add(new BibliographyResponse.BibTeXField("type", enrichedType));
                            enrichedFields.add(new BibliographyResponse.BibTeXField("title", item.getFirstTitle()));
                            enrichedFields.add(new BibliographyResponse.BibTeXField("journal", item.getFirstJournal()));
                            enrichedFields.add(new BibliographyResponse.BibTeXField("author", item.authors() != null && !item.authors().isEmpty() ? item.authors().get(0).given() + " " + item.authors().get(0).family() : "Unknown Author"));
                            enrichedFields.add(new BibliographyResponse.BibTeXField("volume", item.volume() != null ? item.volume() : ""));
                            enrichedFields.add(new BibliographyResponse.BibTeXField("issue", item.issue() != null ? item.issue() : ""));
                            enrichedFields.add(new BibliographyResponse.BibTeXField("pages", item.page() != null ? item.page() : ""));
                            enrichedFields.add(new BibliographyResponse.BibTeXField("publisher", item.getFirstJournal())); // Using journal as publisher for enrichment example
                            enrichedFields.add(new BibliographyResponse.BibTeXField("year", enrichedDate));
                            enrichedFields.add(new BibliographyResponse.BibTeXField("doi", enrichedDoi));
                            BibliographyResponse.Citation enrichedCitation = new BibliographyResponse.  Citation(
                                enrichedType,
                                enrichedDoi, // Example of creating a new citeKey based on type and date
                                enrichedFields
                            );
                            citation.children().add(enrichedCitation);
                            
                            });
                        });
                }
            } catch (Exception e) {
                // Handle individual row resolution timeouts independently
            }
        }
        System.out.println("All enrichment operations completed. Final enriched bibliography: "+extractResults.bibliography().citations().size()); // Debug: Print final enriched bibliography
        // extractResults.bibliography().citations().forEach(citation -> {
        //     System.out.println("Final enriched citation: " + citation.type()+" "+citation.citeKey()+" "+citation.children().size()); // Debug: Print each enriched citation
        //     citation.children().forEach(child -> {
        //         System.out.println("  Child citation: " + child.type()+" "+child.citeKey()+(child.children() != null ? " has " + child.children().size() + " children" : " no children")); // Debug: Print each child citation
        //         if(child.children() != null)
        //             child.children().forEach(grandChild -> {
        //             System.out.println("        Grandchild citation: " + grandChild.type()+" "+grandChild.citeKey()); // Debug: Print each grandchild citation
        //             grandChild.fields().forEach(field -> {
        //                 System.out.println("            Field: " + field.key() + " = " + field.value()); // Debug: Print each field of the grandchild citation
        //             });
        //         });
        //     });
        // });
        messageSender.sendBibtexMessage(extractResults.bibliography());
//    pdfActivities.saveToNeo4jGraph(title, collectedCrossRefMetaData);
    }

    private String helperExtractDoiRegex(String text) {
        // Clean utility to isolate the DOI substring out of raw bibliography layout sentences
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "10.\\d{4,9}/[-._;()/:A-Z0-9]+", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

}
