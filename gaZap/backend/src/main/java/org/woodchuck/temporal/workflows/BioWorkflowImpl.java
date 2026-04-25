package org.woodchuck.temporal.workflows;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Component;
import org.woodchuck.temporal.activities.BioActivities;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

//@Component
//@WorkflowImpl(taskQueues = "BioTaskQueue")
public class BioWorkflowImpl implements BioWorkflow {
    @Override
    public List<String> execute(BioWorkflowRequest request) {
        BioActivities activities = newActivities(request.getSettings());

        if (request.getOperation() == BioWorkflowRequest.Operation.SEARCH) {
            List<String> identifiers = activities.searchIdentifiers(request.getQuery());
            return activities.fetchEntries(identifiers);
        }
        if (request.getOperation() == BioWorkflowRequest.Operation.GET_DATA) {
            return activities.fetchEntries(request.getEntries());
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
