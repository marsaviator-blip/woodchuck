package org.woodchuck.controllers;

import org.woodchuck.dtos.SearchRequest;
import org.woodchuck.dtos.HumanReviewEdits;
import org.woodchuck.dtos.LivePipelineState;
import org.woodchuck.temporal.workflows.ThematicAnalysisWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;

//import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Endpoint layer for managing the Unstructured Thematic Analysis Workflow.
 * Bridges HTTP interactions from the Vue 3 dashboard straight into Temporal execution loops.
 */
@RestController
@RequestMapping("/api/analysis")
// Adjust the allowed origins rule to match your Vue local server (typically port 5173 or 3000)
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ThematicAnalysisController {

    private final WorkflowClient workflowClient;

    /**
     * The temporal-spring-boot-starter automatically makes the thread-safe 
     * WorkflowClient available for constructor injection.
     */
    public ThematicAnalysisController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    /**
     * REST Endpoint 1: Triggers the entire execution pipeline asynchronously.
     * POST /api/analysis/start?subject=Target+Topic
     *
     * @param subject The text topic/subject entered on the UI dashboard
     * @return The unique generated WorkflowID string for frontend tracking loops
     */
    @PostMapping("/start")
    public ResponseEntity<String> startAnalysisPipeline(@RequestBody SearchRequest request) {
        if (request.subjectQuery() == null || request.subjectQuery().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Subject parameter cannot be blank.");
        }

        // Establish a trackable, deterministic business workflow identifier string
        String workflowId = "ThematicAnalysis-" + System.currentTimeMillis();

        // Build stub proxies binding to the task queue defined in application.properties
        ThematicAnalysisWorkflow workflow = workflowClient.newWorkflowStub(
            ThematicAnalysisWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue("analysis-task-queue")
                .build()
        );

        // CRITICAL STEP: Use WorkflowClient.start to trigger processing on a background thread.
        // This ensures the HTTP request returns instantly, preventing network gateway timeouts.
        WorkflowClient.start(workflow::executeAnalysisPipeline, request.subjectQuery().trim(), request.maxLinksToReturn());

        return ResponseEntity.status(HttpStatus.CREATED).body(workflowId);
    }

    /**
     * REST Endpoint 2: Synchronously queries the memory-resident variables of an active pipeline.
     * GET /api/analysis/{workflowId}/status
     *
     * @param workflowId The target execution tracking handle string
     * @return The structured LivePipelineState payload containing progress metrics and temporary themes
     */
    @GetMapping("/{workflowId}/status")
    public ResponseEntity<LivePipelineState> getPipelineStatus(@PathVariable String workflowId) {
        try {
            // Re-fetch a stub proxy pointing to the existing workflow instance running inside Temporal
            ThematicAnalysisWorkflow workflow = workflowClient.newWorkflowStub(ThematicAnalysisWorkflow.class, workflowId);
            
            // Invoke the synchronous @QueryMethod directly
            LivePipelineState snapshotState = workflow.getLiveState();
            
            return ResponseEntity.ok(snapshotState);
        } catch (io.temporal.client.WorkflowNotFoundException e) {
            // Graceful response fallback if an invalid tracking id is parsed
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * REST Endpoint 3: Submits a human modifications payload and unblocks the execution thread.
     * POST /api/analysis/{workflowId}/review
     *
     * @param workflowId The target execution tracking handle string
     * @param edits The JSON mutation payload mapped from card grouping adjustments on the UI
     */
    @PostMapping("/{workflowId}/review")
    public ResponseEntity<Void> submitHumanResponse(@PathVariable String workflowId, @RequestBody HumanReviewEdits edits) {
        if (edits == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Re-fetch a stub proxy pointing to the waiting execution thread
            ThematicAnalysisWorkflow workflow = workflowClient.newWorkflowStub(ThematicAnalysisWorkflow.class, workflowId);
            
            // Fire the asynchronous @SignalMethod. This injects the payload data and 
            // satisfies the internal condition block inside `Workflow.await(...)`
            workflow.submitHumanReview(edits);
            
            return ResponseEntity.accepted().build();
        } catch (io.temporal.client.WorkflowNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

