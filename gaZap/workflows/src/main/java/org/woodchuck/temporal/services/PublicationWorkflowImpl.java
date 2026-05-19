package org.woodchuck.temporal.services;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.woodchuck.temporal.activities.PublicationActivities;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import org.woodchuck.temporal.workflows.PublicationWorkflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import io.temporal.spring.boot.WorkflowImpl;

@Service
@WorkflowImpl(taskQueues = "PublicationQueue")
public class PublicationWorkflowImpl implements PublicationWorkflow {

   private ActivityExecutionSettings settings= new ActivityExecutionSettings();

    @Override
    public String retrieve(String doi) {
           Workflow.await(() -> true);
            PublicationActivities activities = Workflow.newActivityStub(PublicationActivities.class,
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
                    System.out.println("Activity stub for PublicationActivities created.");
            String works = activities.getPublication(doi);
        return works;
    }
}
