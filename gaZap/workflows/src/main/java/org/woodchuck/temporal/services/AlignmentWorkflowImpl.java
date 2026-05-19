package org.woodchuck.temporal.services;

import java.time.Duration;
import java.util.List;

import io.temporal.activity.ActivityOptions;

import org.springframework.stereotype.Service;

import org.woodchuck.temporal.activities.AlignmentActivities;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import org.woodchuck.temporal.workflows.AlignmentWorkflow;

import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import io.temporal.spring.boot.WorkflowImpl;

@Service
@WorkflowImpl(taskQueues = "ALIGNMENT_QUEUE")
public class AlignmentWorkflowImpl implements AlignmentWorkflow {

   private ActivityExecutionSettings settings= new ActivityExecutionSettings();

    @Override
    public String execute(List<String> entries) {
        AlignmentActivities activities = Workflow.newActivityStub(AlignmentActivities.class,
            ActivityOptions.newBuilder()
                // .setStartToCloseTimeout(Duration.ofSeconds(settings.getTimeoutSeconds()))
                    .setStartToCloseTimeout(Duration.ofHours(1))
                    .setHeartbeatTimeout(Duration.ofSeconds(30))
                .setRetryOptions(
                    RetryOptions.newBuilder()
                        .setInitialInterval(Duration.ofSeconds(settings.getInitialIntervalSeconds()))
                            .setBackoffCoefficient(settings.getBackoffCoefficient())
                            .setMaximumInterval(Duration.ofSeconds(settings.getMaximumIntervalSeconds()))
                            .setMaximumAttempts(settings.getMaximumAttempts())
                            .build())
                    .build());
        String result = activities.execute(entries);
        return "Alignment completed for input: " + result;
    }


}
