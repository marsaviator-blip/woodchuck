package org.woodchuck.temporal.workflows;

import org.springframework.core.annotation.Order;
import org.woodchuck.dtos.MaterialStructureParams;
import org.woodchuck.temporal.workflows.specs.MPSpec;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MPWorkflow {
   
    @SignalMethod
    void startUp(MPSpec spec);

    @WorkflowMethod
    void processMP(MPSpec spec);

    @SignalMethod
    void resetFlags();

    @SignalMethod
    void complete();

}


