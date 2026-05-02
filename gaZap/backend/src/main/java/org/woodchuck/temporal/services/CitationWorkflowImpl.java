package org.woodchuck.temporal.services;

import java.time.Duration;

import org.woodchuck.temporal.workflows.ActivityExecutionSettings;

import org.springframework.stereotype.Service;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import org.woodchuck.temporal.activities.CitationActivities;
import io.temporal.workflow.Workflow;

@Service
@WorkflowImpl(taskQueues = "CITATION_QUEUE")
public class CitationWorkflowImpl implements org.woodchuck.temporal.workflows.CitationWorkflow {

    private ActivityExecutionSettings settings= new ActivityExecutionSettings();

    @Override
    public String getCitations(String astmId) {
        CitationActivities activities = Workflow.newActivityStub(CitationActivities.class,
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
         return activities.getCitations(astmId);
    }
}
