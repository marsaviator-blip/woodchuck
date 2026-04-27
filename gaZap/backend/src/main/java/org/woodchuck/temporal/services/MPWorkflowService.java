package org.woodchuck.temporal.services;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.stereotype.Service;
import org.woodchuck.temporal.workflows.MPWorkflow;
import org.woodchuck.temporal.workflows.specs.MPSpec;

@Service
public class MPWorkflowService {
    private final WorkflowClient workflowClient;

    private String wfId;

    public MPWorkflowService(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    public MPWorkflow getWorkflow(String wfId) {
        return workflowClient.newWorkflowStub(MPWorkflow.class, wfId);
    }

    public String createMPWorkflow(MPSpec mpSpec) {
        var uuid = UUID.randomUUID();
        var wf = workflowClient.newWorkflowStub(
            MPWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue("MP_QUEUE")
                .setWorkflowId(uuid.toString())
                .build());
//        WorkflowClient.execute(wf::processMP, mpSpec);
        var execution = WorkflowClient.start(wf::startUp, mpSpec);
        this.wfId = execution.getWorkflowId();
       // WorkflowClient.execute(wf::processMP, mpSpec);  
        return wfId;
    }

    public String getWorkflowId() {
        return wfId;
    }
}