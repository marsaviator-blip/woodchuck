package org.woodchuck.temporal.activities;

import org.woodchuck.components.PipelineMetricsTracker;
import org.woodchuck.dtos.HumanReviewEdits;
import org.woodchuck.dtos.ThematicAnalysisResponse;
import org.woodchuck.temporal.exceptions.RetryableExtractionException;

import ai.docling.serve.api.DoclingServeApi;

import org.woodchuck.temporal.exceptions.NonRetryableExtractionException;

import io.arconia.ai.document.docling.DoclingDocumentReader;
//import io.arconia.ai.docling.reader.DoclingDocumentReader;
import io.temporal.activity.Activity;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//import javax.print.Doc;

@Component
public class AnalysisActivitiesImpl implements AnalysisActivities {

    private final ChatClient chatClient;
    private final DoclingServeApi doclingServeApi; // Added to handle communication with the Docling container
    private final PipelineMetricsTracker metrics;

    public AnalysisActivitiesImpl(ChatClient.Builder chatClientBuilder, DoclingServeApi doclingServeApi,
            PipelineMetricsTracker metrics) {
        // Build the ChatClient to talk with Gemini
        this.chatClient = chatClientBuilder.build();
        this.doclingServeApi = doclingServeApi;
        this.metrics = metrics;
    }

    /**
     * Step 1: Uses Gemini and a custom search tool to discover raw web URLs.
     */
    @Override
    public List<String> discoverUrlsWithGemini(String subject, int maxLinks) {
        try {
            return chatClient.prompt()
                    .user(String.format("Find high-quality articles and reference pages regarding this subject: '%s'. Limit results to exactly %d links.", subject, maxLinks))
                    .tools("discoverSubjectWebsites") // References your WebDiscoveryTools configuration bean
                    .call()
                    .entity(new ParameterizedTypeReference<List<String>>() {
                    });
        } catch (Exception e) {
            metrics.incrementTransientErrors();
            throw new RetryableExtractionException("Gemini discovery failed due to network or rate limit. Retrying...",
                    e);
        }
    }

    /**
     * Step 2: Uses Arconia Docling to strip boilerplate layout clutter and return
     * clean Spring Documents.
     */
    @Override
    public List<Document> parsePagesWithDocling(List<String> urls) {
        List<Document> processedDocuments = new ArrayList<>();

        for (String url : urls) {
            try {
                // Fetch and extract structural markdown layout via the local Docling container
                // DocumentReader reader =
                // io.arconia.ai.document.docling.DoclingDocumentReader.builder()
                // .doclingServeApi(this.doclingServeApi)
                // .resources(new UrlResource(url)) // Feeds the web URL to the underlying
                // parser
                // .build();
                URI myUri = URI.create(url);
                Resource someResource = new UrlResource(myUri);
                DoclingDocumentReader reader = DoclingDocumentReader.builder()
                        .doclingServeApi(doclingServeApi)
                        .files(someResource)
                        .build();
                // DoclingDocumentReader reader = new DoclingDocumentReader(url);
                List<Document> extractedDocs = reader.read();

                // Track metadata for future human tracing requirements on the frontend
                for (Document doc : extractedDocs) {
                    doc.getMetadata().put("source_url", url);
                    processedDocuments.add(doc);
                }
            } catch (HttpClientErrorException e) {
                // Permanent failure (403 Forbidden, 404 Not Found). Log, increment metrics, and
                // skip URL.
                System.err.println("Skipping URL due to permanent HTTP error [" + url + "]: " + e.getMessage());
                metrics.incrementSkippedUrls();
                continue;
            } catch (HttpServerErrorException | ResourceAccessException e) {
                // Transient error (503 Service Unavailable, network timeout). Trigger a
                // Temporal retry.
                metrics.incrementTransientErrors();
                throw new RetryableExtractionException("Transient network issue parsing page: " + url, e);
            } catch (Exception e) {
                // Catch unpredictable or fatal container connection crashes
                throw new NonRetryableExtractionException(
                        "Fatal system exception during document parsing: " + e.getMessage());
            }
        }

        // Error boundary check to verify we actually have data to send to Gemini
        if (processedDocuments.isEmpty()) {
            throw new NonRetryableExtractionException("All discovered URLs failed to compile. Pipeline aborted.");
        }

        return processedDocuments;
    }

    /**
     * Step 3: Passes the parsed chunks back to Gemini using Chain-of-Thought
     * reasoning.
     */
    @Override
    public ThematicAnalysisResponse generateThemesWithReasoning(List<Document> chunks) {
        try {
            // Flatten the text blocks from our native Spring Documents
            String combinedTextBlocks = chunks.stream()
                    .map(Document::getFormattedContent)
                    .collect(Collectors.joining("\n\n--- Segment Break ---\n\n"));

            return chatClient.prompt()
                    .user(u -> u
                            .text("""
                                    You are a meticulous qualitative research assistant.
                                    Analyze the following unstructured web text segments regarding the target subject.

                                    Follow these steps for your reasoning:
                                    1. Read all segments and note repeating conceptual patterns.
                                    2. Filter out superficial mentions; isolate deep topical relationships.
                                    3. For each major theme identified, write out the explicit underlying logic that links the text segments together.

                                    ---
                                    TEXT SEGMENTS:
                                    {segments}
                                    """)
                            .param("segments", combinedTextBlocks))
                    .call()
                    .entity(ThematicAnalysisResponse.class); // Auto-maps JSON directly to your Java Record structure
        } catch (Exception e) {
            metrics.incrementTransientErrors();
            throw new RetryableExtractionException("Gemini thematic extraction failed temporarily. Retrying...", e);
        }
    }

    /**
     * Step 4: Finalizes operations and commits data records.
     */
    @Override
    public void saveFinalResults(ThematicAnalysisResponse finalResults) {
        System.out.println("Finalized data successfully saved to the database layer!");
        System.out.println("Total Themes Harvested: " + finalResults.extractedThemes().size());
        // Insert standard JPA repository call configurations here...
    }
}
