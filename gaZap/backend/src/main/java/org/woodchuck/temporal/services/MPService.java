package org.woodchuck.temporal.services;

import java.util.UUID;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.stereotype.Service;
import org.woodchuck.temporal.worker.MPWorkflow;
import org.woodchuck.dtos.MaterialStructureParams;
import org.woodchuck.temporal.workflows.MPSpec;

@Service
public class MPService {
    private final WorkflowClient workflowClient;

    public MPService(WorkflowClient workflowClient) {
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