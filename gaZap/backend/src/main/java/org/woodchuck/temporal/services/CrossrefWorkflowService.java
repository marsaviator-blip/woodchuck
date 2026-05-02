package org.woodchuck.temporal.services;

import io.temporal.client.WorkflowClient;

import org.springframework.stereotype.Service;
import io.temporal.client.WorkflowOptions;
import org.woodchuck.temporal.workflows.CrossrefWorkflow;

@Service
public class CrossrefWorkflowService {
    private final WorkflowClient workflowClient;

    private String wfId;

    public CrossrefWorkflowService(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    public String getWorkflowId() {
        return wfId;
    }

    public CrossrefWorkflow getWorkflow(String wfId) {
        return workflowClient.newWorkflowStub(CrossrefWorkflow.class, wfId);
    }

    public String createCrossrefWorkflow(String doi) {
        var uuid = java.util.UUID.randomUUID();
        var wf = workflowClient.newWorkflowStub(
            CrossrefWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue("CrossrefQueue")
                .setWorkflowId(uuid.toString())
                .build());
        System.out.println("Created CrossrefWorkflow with ID: " + uuid.toString());
        var execution = WorkflowClient.start(wf::cross, doi);
        this.wfId = execution.getWorkflowId();
        return wfId;
    }
}
