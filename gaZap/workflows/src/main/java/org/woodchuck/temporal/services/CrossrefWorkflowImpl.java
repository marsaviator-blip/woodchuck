package org.woodchuck.temporal.services;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jbibtex.BibTeXDatabase;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

import org.woodchuck.converter.CrossrefSearchToBibTeXDatabase;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import org.woodchuck.temporal.workflows.CrossrefWorkflow;
import org.woodchuck.dtos.CitedReferencesResult;

import org.woodchuck.dtos.CrossrefXmlResponse;
import org.woodchuck.dtos.DiscoveryTarget;
import org.woodchuck.temporal.activities.CrossrefActivities;
import org.woodchuck.temporal.activities.CrossrefDatabaseActivities;

@Service
@WorkflowImpl(taskQueues = "CrossrefQueue")
public class CrossrefWorkflowImpl implements org.woodchuck.temporal.workflows.CrossrefWorkflow {

    private ActivityExecutionSettings settings= new ActivityExecutionSettings();


    // Assuming you have an API activity to fetch the XML as well
    // private final CrossrefApiActivities apiActivities = 
    //     Workflow.newActivityStub(CrossrefApiActivities.class, /* options */);

    @Override
    public void startUp(String doi) {
        Workflow.await(() -> true);
        System.out.println("CrossrefWorkflowImpl started up with DOI: " + doi);
    }

    @Override
    public String execute(String doi, String author, String title, int generations) {
        Workflow.await(() -> true);
        CrossrefActivities activities = Workflow.newActivityStub(CrossrefActivities.class,
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
                System.out.println("Activity stub for CrossrefActivities created.");
        CrossrefDatabaseActivities dbActivities =
            Workflow.newActivityStub(
                CrossrefDatabaseActivities.class,
                ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .build());
        Queue<DiscoveryTarget> queue = new LinkedList<>();
        Set<String> processedDois = new HashSet<>();

        System.out.println("Starting Crossref workflow loop with initial DOI: " + doi+" author:"+author+" title:"+title);
        // Seed the loop with the initial target document
        queue.add(new DiscoveryTarget(null, doi, author, title, 0));
        
        String primaryResponse = null;
        while (!queue.isEmpty()) {
            DiscoveryTarget current = queue.poll();

            // Guard against duplicate network calls or exceeding the generational depth limit
            if (processedDois.contains(current.doi()) || current.generation() > generations) {
                continue;
            }
            processedDois.add(current.doi());

            CitedReferencesResult citedReferencesResult;

            // 4. Use your exact activity logic based on criteria completeness
            if ((current.author() == null || current.author().isEmpty()) && 
                (current.title() == null || current.title().isEmpty())) {
                
                // For pure citations down the tree, use your single DOI activity call
                // Note: If getWorks only returns CrossrefXmlResponse, you'll need to wrap its 
                // reference parsing step inside an activity so the workflow can safely extract children!
                boolean isCached = dbActivities.isRecordCached(current.doi());
                
                if (isCached) {
                    System.out.println("Cache HIT for DOI: " + current.doi() + ". Skipping external API call.");
                    citedReferencesResult = dbActivities.findCachedRecord(current.doi(), current.doi());
                } else {
                    citedReferencesResult = activities.getWorks(current.doi());
                    dbActivities.saveResponse(current.doi(), current.title(), citedReferencesResult.getRawXml(), citedReferencesResult.getRawJson());
                    // Keep API scrape bursts friendly to Crossref's rate limits
                    Workflow.sleep(Duration.ofMillis(250));
                }
                if (primaryResponse == null) {
                    primaryResponse = citedReferencesResult.getRawXml();
                }
                 
                
            } else {
                // For the primary document or rich search records, call your getWorksBy activity
                // IMPORTANT: Drop the 'generations' argument from the activity method signature 
                // so the activity doesn't try to recurse internally anymore!
                    boolean isCached = dbActivities.isRecordCached(current.doi());
                    
                    if (isCached) {
                        System.out.println("Cache HIT for DOI: " + current.doi() + ". Skipping external API call.");
                        citedReferencesResult = dbActivities.findCachedRecord(current.doi(), current.doi());; 
                    }
                    else{
                        citedReferencesResult = activities.getWorksBy(current.doi(), current.author(), current.title());
                        dbActivities.saveResponse(current.doi(), current.title(), citedReferencesResult.getRawXml(), citedReferencesResult.getRawJson());
                        // Keep API scrape bursts friendly to Crossref's rate limits
                        Workflow.sleep(Duration.ofMillis(250));
                    }
                    if (primaryResponse == null) {
                    primaryResponse = citedReferencesResult.getRawJson();
                }
            }

            List<String> referenceDois = citedReferencesResult.getExtractedReferenceDois();
            // 5. Append children and grandchildren to the execution queue dynamically
            System.out.println("Extracted reference DOIs: ");
            if (current.generation() < generations && !referenceDois.isEmpty()) {
                for (String referenceDoi : referenceDois) {
                    // boolean isCached = dbActivities.isRecordCached(referenceDoi);
                    
                    // if (isCached) {
                    //     System.out.println("Cache HIT for DOI: " + referenceDoi + ". Skipping external API call.");
                    //     // Optional: If you need to extract child references from a cached item,
                    //     // you would call a "fetchCachedRecord" activity here instead of skipping completely.
                    //     continue; 
                    // }
                    if (!processedDois.contains(referenceDoi) && referenceDoi != null) {
                        // Push found reference down into the queue, incrementing the generation track
                        queue.add(new DiscoveryTarget(current.doi(), referenceDoi, null, null, current.generation() + 1));
                    }
                }
            }
            System.out.println("queue size " + queue.size());
        }

        return primaryResponse;
    }

    public String getCrossrefResult() {
        // Implementation for retrieving the Crossref result based on the citeKey
        return null; // Replace with actual retrieval logic
    }
}
