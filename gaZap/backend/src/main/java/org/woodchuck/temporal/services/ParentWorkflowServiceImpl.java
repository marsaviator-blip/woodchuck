package org.woodchuck.temporal.services;

import org.springframework.stereotype.Service;
import org.woodchuck.temporal.workflows.BioWorkflow;
import org.woodchuck.temporal.workflows.MPWorkflow;
import org.woodchuck.temporal.workflows.ParentWorkflow;
import org.woodchuck.temporal.workflows.specs.BioWorkflowRequest;
import org.woodchuck.temporal.workflows.specs.MPSpec;

import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;

@Service
public class ParentWorkflowServiceImpl implements ParentWorkflow{

     public void startBioWorkflow(BioWorkflowRequest request) {

        ChildWorkflowOptions bioOptions = ChildWorkflowOptions.newBuilder()
                .setWorkflowId("bio-workflow-" + System.currentTimeMillis())
                .setTaskQueue("BIO_QUEUE")
                .build();

        BioWorkflow bioWorkflow = Workflow.newChildWorkflowStub(BioWorkflow.class, bioOptions);
        bioWorkflow.execute(request);
     }

     public void startMPWorkflow(MPSpec spec) {
         ChildWorkflowOptions mpOptions = ChildWorkflowOptions.newBuilder()
                .setWorkflowId("mp-workflow-" + System.currentTimeMillis())
                .setTaskQueue("MP_QUEUE")
                .build();
        MPWorkflow mpWorkflow = Workflow.newChildWorkflowStub(MPWorkflow.class, mpOptions);
        mpWorkflow.processMP(spec);
     }
}