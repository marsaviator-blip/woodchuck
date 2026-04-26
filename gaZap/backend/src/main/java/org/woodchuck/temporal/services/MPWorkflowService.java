package org.woodchuck.temporal.services;

import java.util.UUID;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.stereotype.Service;
import org.woodchuck.temporal.workflows.MPWorkflow;
import org.woodchuck.temporal.workflows.specs.MPSpec;

@Service
public class MPWorkflowService {
    private final WorkflowClient workflowClient;

    public MPWorkflowService(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    public MPWorkflow getWorkflow(String mpExecutionId) {
        return workflowClient.newWorkflowStub(MPWorkflow.class, mpExecutionId);
    }

    public String createMPWorkflow(MPSpec mpSpec) {
        var uuid = UUID.randomUUID();
        var wf = workflowClient.newWorkflowStub(
            MPWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue("MP_QUEUE")
                .setWorkflowId(uuid.toString())
                .build());
        var execution = WorkflowClient.start(wf::processMP, mpSpec);
        return execution.getWorkflowId();
    }
}