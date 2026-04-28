package org.woodchuck.temporal.services;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.woodchuck.temporal.activities.BioActivities;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import org.woodchuck.temporal.workflows.BioWorkflow;
import org.woodchuck.temporal.workflows.specs.BioWorkflowRequest;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

@Service
@WorkflowImpl(taskQueues = "BioTaskQueue")
public class BioWorkflowImpl implements BioWorkflow {

    private boolean isStarted = false;  
    private boolean notProcessed = true;
    private boolean processed = false;
    private boolean completed = false;
    private BioActivities activities;

   public void resetFlags() {
        Workflow.await(() -> processed); 
        this.isStarted = false;
        this.notProcessed = true;
        this.processed = false;
    }   

    public void complete() {
        Workflow.await(() -> processed);
        this.isStarted = true;
        this.completed = true;
    }

    public void startUp(BioWorkflowRequest request) {
        Workflow.await(() -> notProcessed);
         activities = newActivities(request.getSettings());
      
        isStarted = true;  
        Workflow.await(() -> processed); 
        System.out.println("BioWorkflowImpl started up.");
    }  

    @Override
    public List<String> execute(BioWorkflowRequest request) {
        Workflow.await(() -> isStarted);
        //activities = newActivities(request.getSettings());

        if (request.getOperation() == BioWorkflowRequest.Operation.SEARCH) {
            List<String> identifiers = activities.searchIdentifiers(request.getQuery());
            List<String> entries = activities.fetchEntries(identifiers);
            notProcessed = false;
            processed = true;

            return entries;
        }
        if (request.getOperation() == BioWorkflowRequest.Operation.GET_DATA) {
            List<String> entries = activities.fetchEntries(request.getEntries());
            notProcessed = false;
            processed = true;
            return entries;
        }

        throw new IllegalArgumentException("Unsupported workflow operation: " + request.getOperation());
    }

    private BioActivities newActivities(ActivityExecutionSettings settings) {
        return Workflow.newActivityStub(
            BioActivities.class,
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
    }

}
