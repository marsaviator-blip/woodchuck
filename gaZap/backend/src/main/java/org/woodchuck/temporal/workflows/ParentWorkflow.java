package org.woodchuck.temporal.workflows;

// import org.woodchuck.temporal.workflows.specs.BioWorkflowRequest;
// import org.woodchuck.temporal.workflows.specs.MPSpec;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ParentWorkflow {

    @WorkflowMethod
    void startBioWorkflow();  

    @WorkflowMethod 
    void startMPWorkflow();
}
