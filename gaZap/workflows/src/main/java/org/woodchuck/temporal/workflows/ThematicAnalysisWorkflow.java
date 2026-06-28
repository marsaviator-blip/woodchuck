package org.woodchuck.temporal.workflows;

import org.woodchuck.dtos.HumanReviewEdits;
import org.woodchuck.dtos.LivePipelineState;
import org.woodchuck.dtos.ThematicAnalysisResponse;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ThematicAnalysisWorkflow {

    @WorkflowMethod
    void executeAnalysisPipeline(String subjectOfInterest);

    // This allows a React or Vaadin UI to asynchronously inject human edits
    @SignalMethod
    void submitHumanReview(HumanReviewEdits edits);

    /**
     * Synchronous Query method to read the memory-resident status variables of the pipeline.
     * Called by the Spring Boot controller to provide live status updates to the Vue 3 frontend.
     *
     * @return a snapshot of the pipeline progress, discovered URLs, and generated themes
     */
    @QueryMethod
    LivePipelineState getLiveState();
}