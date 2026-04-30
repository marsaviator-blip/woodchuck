package org.woodchuck.temporal.services;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import org.woodchuck.dtos.CrossrefRecord;
import org.woodchuck.temporal.activities.CrossrefActivities;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
        public CrossrefRecord execute(String doi) {
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
            String works = activities.getWorks(doi);
            System.out.println("Received works data: " + works);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(works);
            JsonNode messageNode = rootNode.path("message");
            JsonNode issnNode = messageNode.path("ISSN");
            List<String> issnData = new java.util.ArrayList<>();
            for (JsonNode node : issnNode) {
                String issn = node.asString();
                issnData.add(issn);
            }
            System.out.println("Extracted ISSN data: " + issnData);
            JsonNode referencesNode = messageNode.path("reference");
            List<String> doiData = new java.util.ArrayList<>();
            if (referencesNode != null && referencesNode.isArray()) {
            for (JsonNode node : referencesNode) {
                if(node.has("DOI")) {
                    String referenceDoi = node.path("DOI").asString();
                    doiData.add(referenceDoi);
                }
            }
            }
            System.out.println("Extracted DOI data: " + doiData);
            CrossrefRecord record = new CrossrefRecord(issnData, doiData);
            System.out.println("Created CrossrefRecord: " + record);
            return record;
        }

}
