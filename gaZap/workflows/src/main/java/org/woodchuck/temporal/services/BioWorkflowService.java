package org.woodchuck.temporal.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.woodchuck.temporal.workflows.specs.BioWorkflowRequest;
import org.woodchuck.temporal.workflows.BioWorkflow;
import org.woodchuck.temporal.workflows.PublicationWorkflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;

@Service
public class BioWorkflowService {
    private final WorkflowClient workflowClient;

    private String wfId;
    private String publicationWfId;

    public BioWorkflowService(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    public BioWorkflow getWorkflow(String wfId) {
        return workflowClient.newWorkflowStub(BioWorkflow.class, wfId);
    }

    public String createBioWorkflow(BioWorkflowRequest bioSpec) {
        var uuid = UUID.randomUUID();
        var wf = workflowClient.newWorkflowStub(
            BioWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue("BioTaskQueue")
                .setWorkflowId(uuid.toString())
                .build());
        var execution = WorkflowClient.start(wf::execute, bioSpec);
        this.wfId = execution.getWorkflowId();
       // WorkflowClient.execute(wf::processMP, mpSpec);  
        return wfId;
    }

    public String getWorkflowId() {
        return wfId;
    }

   public String createPublicationWorkflow(String doi) {
        var uuid = UUID.randomUUID();
        var wf = workflowClient.newWorkflowStub(
            PublicationWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue("PublicationQueue")
                .setWorkflowId(uuid.toString())
                .build());
        var execution = WorkflowClient.start(wf::retrieve, doi);
        this.publicationWfId = execution.getWorkflowId();
       // WorkflowClient.execute(wf::processMP, mpSpec);  
        return publicationWfId;
    }

    public String getPublicationWorkflowId() {
        return publicationWfId;
    }
}
