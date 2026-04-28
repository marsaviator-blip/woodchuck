package org.woodchuck.temporal.workflows;

import java.util.List;

import org.woodchuck.temporal.workflows.specs.BioWorkflowRequest;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface BioWorkflow {

    @SignalMethod
    void startUp(BioWorkflowRequest request);

    @WorkflowMethod
    List<String> execute(BioWorkflowRequest request);

    @SignalMethod
    void resetFlags();

    @SignalMethod
    void complete();

}
