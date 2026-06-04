package org.woodchuck.temporal.services;

import java.time.Duration;

import org.jbibtex.BibTeXDatabase;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

import org.woodchuck.converter.CrossrefSearchToBibTeXDatabase;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import org.woodchuck.dtos.CitedReferencesResult;
import org.woodchuck.dtos.CrossrefXmlResponse;
import org.woodchuck.temporal.activities.CrossrefActivities;

@Service
@WorkflowImpl(taskQueues = "CrossrefQueue")
public class CrossrefWorkflowImpl implements org.woodchuck.temporal.workflows.CrossrefWorkflow {

    private ActivityExecutionSettings settings= new ActivityExecutionSettings();

    @Override
    public void startUp(String doi) {
        Workflow.await(() -> true);
        System.out.println("CrossrefWorkflowImpl started up with DOI: " + doi);
    }

    @Override
    public CrossrefXmlResponse execute(String doi, String author, String title) {
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
        if((author == null || author.isEmpty()) && (title == null || title.isEmpty())) {
            return activities.getWorks(doi);
        } else {
            CitedReferencesResult citedReferencesResult = activities.getWorksBy(doi, author, title);
            CrossrefXmlResponse response = CrossrefXmlResponse.fromSearchResponse(citedReferencesResult.crossrefSearchResponse());
            BibTeXDatabase bibDatabase = CrossrefSearchToBibTeXDatabase
                .fromSearchResponse(citedReferencesResult.crossrefSearchResponse());
            System.out.println("CrossrefWorkflowImpl generated BibTeX entries: " + bibDatabase.getEntries().size());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.valueToTree(bibDatabase);
                //objectMapper.writeValue(System.out, rootNode);

            } catch (Exception e) {
                System.err.println("Error serializing BibTeXDatabase: " + e.getMessage());
            }
            return response;
        }
        // System.out.println("Received works data: " + works);
        // ObjectMapper objectMapper = new ObjectMapper();
        // JsonNode rootNode = objectMapper.readTree(works);
        // JsonNode messageNode = rootNode.path("message");
        // JsonNode issnNode = messageNode.path("ISSN");
        // List<String> issnData = new java.util.ArrayList<>();
        // for (JsonNode node : issnNode) {
        //     String issn = node.asString();
        //     issnData.add(issn);
        // }
        // System.out.println("Extracted ISSN data: " + issnData);
        // JsonNode referencesNode = messageNode.path("reference");
        // List<String> doiData = new java.util.ArrayList<>();
        // if (referencesNode != null && referencesNode.isArray()) {
        // for (JsonNode node : referencesNode) {
        //     if(node.has("DOI")) {
        //         String referenceDoi = node.path("DOI").asString();
        //         doiData.add(referenceDoi);
        //     }
        // }
        // }
        // System.out.println("Extracted DOI data: " + doiData);
        // CrossrefRecord record = new CrossrefRecord(issnData, doiData);
        // System.out.println("Created CrossrefRecord: " + record);
        // return record;
    }

}
