package org.woodchuck.temporal.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import io.temporal.workflow.Promise;

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
import org.woodchuck.dtos.DocumentAnalysisResult;

@Service
@WorkflowImpl(taskQueues = "IngestionQueue")
public class PdfIngestionWorkflowImpl implements PdfIngestionWorkflow {

   private ActivityExecutionSettings settings= new ActivityExecutionSettings();

    private PdfIngestionActivities pdfActivities;
    private CrossrefActivities crossrefActivities;
                    
    @Override
    public void execute(String pdfFilePath, String title) {
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
                    .setStartToCloseTimeout(Duration.ofSeconds(settings.getTimeoutSeconds()))
                     .setRetryOptions(
                        RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(settings.getInitialIntervalSeconds()))
                            .setBackoffCoefficient(settings.getBackoffCoefficient())
                            .setMaximumInterval(Duration.ofSeconds(settings.getMaximumIntervalSeconds()))
                            .setMaximumAttempts(settings.getMaximumAttempts())
                            .build())
                    .build());

        DocumentAnalysisResult analysisResult = pdfActivities.extractReferenceSection(pdfFilePath);
        List<String> individualCitations = pdfActivities.splitReferences(analysisResult);
    //     List<Promise<String>> crossRefPromises = new ArrayList<>();

    //     for (String line : individualCitations) {
    //         String extractedDoi = helperExtractDoiRegex(line);
    //         if (extractedDoi != null) {
    //             // Async execution: Schedules activity immediately without waiting for it to finish
    //             Promise<String> promise = Async.function(crossrefActivities::getWorks, extractedDoi);
    //             crossRefPromises.add(promise);
    //         }
    //         else {
    //             // Handle lines where no DOI could be extracted
                
    //         }
    //     }

    //     // Block and await completion for all parallel operations
    //     List<String> collectedCrossRefMetaData = new ArrayList<>();
    //     for (Promise<String> promise : crossRefPromises) {
    //         try {
    //             String result = promise.get(); // Retrieves result or catches exception if single activity errored
    //             if (result != null) collectedCrossRefMetaData.add(result);
    //         } catch (Exception e) {
    //             // Handle individual row resolution timeouts independently
    //         }
    //     }
    
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
